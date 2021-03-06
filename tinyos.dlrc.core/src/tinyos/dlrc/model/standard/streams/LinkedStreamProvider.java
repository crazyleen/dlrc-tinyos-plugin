/*
 * Dlrc 2, NesC development in Eclipse.
 * Copyright (C) 2010 DLRC
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Web:  http://tos-ide.ethz.ch
 * Mail: tos-ide@tik.ee.ethz.ch
 */
package tinyos.dlrc.model.standard.streams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import tinyos.dlrc.TinyOSPlugin;
import tinyos.dlrc.ep.IParseFile;
import tinyos.dlrc.model.ProjectModel;
import tinyos.dlrc.model.standard.IStreamProvider;
import tinyos.dlrc.utility.IntList;

/**
 * {@link IStreamProvider} using one cache file for each {@link IParseFile}.
 * @author Benjamin Sigg
 */
public class LinkedStreamProvider extends StreamProvider{
	private Map<ICacheFile, MapFile> openFiles = new HashMap<ICacheFile, MapFile>();
	
	/** cache for the last file that has been accessed */
	private MapFile lastReadFile = null;
	
	private IStreamConverter converter;
	
	public LinkedStreamProvider( ProjectModel model, IStreamConverter streamConverter, IPathConverter pathConverter ){
		super( model, pathConverter );
		this.converter = streamConverter;
	}
	
	private void setLastReadFile( MapFile map ){
		if( lastReadFile != map ){
			if( lastReadFile != null ){
				if( !lastReadFile.isOpen() ){
					MapFile temp = lastReadFile;
					lastReadFile = null;
					remove( temp );
				}
			}
			lastReadFile = map;
		}
	}
	
	private void remove( MapFile map ){
		ICacheFile file = map.getKey();
		openFiles.remove( file );
		if( map != lastReadFile ){
			file.close();	
		}
	}
	
	private synchronized MapFile open( IParseFile file, String extension ) throws CoreException{
		ICacheFile cache = getDerivedFile( file, extension );
		if( cache == null )
			return null;
		
		MapFile map = openFiles.get( cache );
		if( map == null ){
			map = new MapFile( cache );
			if( cache.exists() ){
				map.read();
			}
			openFiles.put( cache, map );
		}
		map.open();
		return map;
	}
	
	public boolean canRead( IParseFile file, String extension ){
		try{
			MapFile map = open( file, extension );
			setLastReadFile( map );
			if( map == null ){
				return false;
			}
			boolean result = map.contains( extension );
			map.close();
			return result;
		}
		catch( CoreException e ){
			return false;
		}
	}

	public InputStream read( IParseFile file, String extension ) throws CoreException{
		MapFile map = open( file, extension );
		if( map == null ){
			return null;
		}
		
		byte[] content = map.get( extension );
		map.close();
		
		if( content == null )
			return null;
		
		return new ByteArrayInputStream( content );
	}

	public void clear( IParseFile file, String extension, IProgressMonitor monitor ){
		try{
			MapFile map = open( file, extension );
			setLastReadFile( map );
			if( map != null ){
				map.remove( extension );
				map.close();
			}
		}
		catch( CoreException ex ){
			TinyOSPlugin.log( ex );
		}
	}

	public OutputStream write( IParseFile file, String extension ) throws CoreException{
		MapFile map = open( file, extension );
		if( map == null )
			return null;
		if( lastReadFile == map )
			setLastReadFile( null );
		MapOutputStream out = new MapOutputStream( extension, map );
		return out;
	}
	
	@Override
	protected IPath derivedFilePath( File file, String extension ){
		return new Path( file.getAbsolutePath() + ".cache" );
	}
	
	private class MapOutputStream extends ByteArrayOutputStream{
		private String extension;
		private MapFile file;
		
		public MapOutputStream( String extension, MapFile file ){
			this.extension = extension;
			this.file = file;
		}
		
		@Override
		public void close() throws IOException{
			super.close();
			
			if( file != null ){
				file.put( extension, toByteArray() );
				file.close();
				file = null;
			}
		}
	}
	
	private class MapFile{
		private Map<String, byte[]> mapping = new HashMap<String, byte[]>();
		private ICacheFile file;
		private volatile int openCount = 0;
		private volatile boolean modified = false;
		
		public MapFile( ICacheFile file ){
			this.file = file;
		}
		
		public ICacheFile getKey(){
			return file;
		}
		
		public boolean isOpen(){
			return openCount > 0;
		}
		
		public synchronized void open(){
			openCount++;
		}
		
		public synchronized void close(){
			openCount--;
			if( openCount == 0 ){
				if( this != lastReadFile ){
					LinkedStreamProvider.this.remove( this );
				}
				if( modified ){
					try{
						write( null );
						modified = false;
					}
					catch( CoreException e ){
						TinyOSPlugin.log( e );
					}
				}
			}
		}
		
		public synchronized void put( String key, byte[] data ){
			mapping.put( key, data );
			modified = true;
		}
		
		public synchronized void remove( String key ){
			mapping.remove( key );
			modified = true;
		}
		
		public synchronized byte[] get( String key ){
			return mapping.get( key );
		}
		
		public synchronized boolean contains( String key ){
			return mapping.containsKey( key );
		}
		
		public synchronized void read() throws CoreException{
			if( !file.isAccessible() || !file.exists() )
				return;
			
			List<String> extensions = new ArrayList<String>();
			IntList sizes = new IntList();
			
			try{
			    DataInputStream in = new DataInputStream( converter.convert( file.getContents() ) );
			 
			    // read header
			    int version = in.readInt();
			    if( version != 1 ){
			    	in.close();
			    	return;
			    }
			    
			    int entries = in.readInt();
			    for( int i = 0; i < entries; i++ ){
			    	extensions.add( in.readUTF() );
			    	sizes.add( in.readInt() );
			    }
			    
			    // read extensions
			    for( int i = 0; i < entries; i++ ){
			    	int size = sizes.get( i );
			    	byte[] data = new byte[ size ];
			    	
			    	int read = 0;
			    	int offset = 0;
			    	
			    	while( offset < size && (read = in.read( data, offset, size-offset )) > 0 ){
			    		offset += read;
			    	}
			    	
			    	mapping.put( extensions.get( i ), data );
			    }
			    
			    in.close();
			}
			catch( IOException ex ){
				TinyOSPlugin.warning( ex );
			}
		}
		
		public synchronized void write( IProgressMonitor monitor ) throws CoreException{
			try{
				if( mapping.isEmpty() ){
					clearFile( file, monitor );
				}
				else{
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					DataOutputStream out = new DataOutputStream( converter.convert( bout ));
					
					// header
					out.writeInt( 1 );
					out.writeInt( mapping.size() );
					for( Map.Entry<String, byte[]> entry : mapping.entrySet() ){
						out.writeUTF( entry.getKey() );
						out.writeInt( entry.getValue().length );
					}
					
					// content
					for( Map.Entry<String, byte[]> entry : mapping.entrySet() ){
						out.write( entry.getValue() );
					}
					
					out.close();
					
					ByteArrayInputStream in = new ByteArrayInputStream( bout.toByteArray() );
					create( file, in, monitor );
					in.close();
				}
			}
			catch( IOException ex ){
				throw new CoreException( new Status( IStatus.ERROR, TinyOSPlugin.PLUGIN_ID, ex.getMessage(), ex ) );
			}
		}
	}
}
