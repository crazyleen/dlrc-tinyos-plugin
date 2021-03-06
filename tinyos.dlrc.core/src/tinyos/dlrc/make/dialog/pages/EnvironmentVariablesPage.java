/*
 * Dlrc 2, NesC development in Eclipse.
 * Copyright (C) 2009 DLRC
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
package tinyos.dlrc.make.dialog.pages;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

import tinyos.dlrc.editors.NesCIcons;
import tinyos.dlrc.make.EnvironmentVariable;
import tinyos.dlrc.make.targets.MakeTargetPropertyKey;

public class EnvironmentVariablesPage extends KeyValuePage<EnvironmentVariable>{
	public EnvironmentVariablesPage( boolean showCustomization ){
		super( showCustomization, "Environment Variables", "Environment variables set before building the application. These variables will override any other variable, including variables normally set by Eclipse like 'TOSROOT'." );
        setImage( NesCIcons.icons().get( NesCIcons.ICON_ENVIRONMENT_VARIABLE ) );
	}
	
	@Override
	public String checkValid( String[][] table ){
		return null;
	}
	
	@Override
	protected KeyValueDialog<EnvironmentVariable> createDialog( Shell shell ){
		return new KeyValueDialog<EnvironmentVariable>( shell, this ){
			@Override
			protected EnvironmentVariable create( String key, String value ){
				return new EnvironmentVariable( key, value );
			}
			@Override
			protected boolean checkOk( String key, String value ){
				return key.length() > 0;
			}
		};
	}

	@Override
	protected EnvironmentVariable create( String key, String value, TableItem row ){
		return new EnvironmentVariable( key, value );
	}

	@Override
	public String getDialogExample(){
		return "Example: 'CLASSPATH=/opt/java/tinyos.jar' is 'Variable: CLASSPATH', 'Value: /opt/java/tinyos.jar'";
	}

	@Override
	public String getEditDialogTitle(){
		return "Edit environment variable";
	}

	@Override
	protected String getKey( EnvironmentVariable entry ){
		return entry.getKey();
	}

	@Override
	protected MakeTargetPropertyKey<EnvironmentVariable[]> getKey(){
		return MakeTargetPropertyKey.ENVIRONMENT_VARIABLES;
	}

	@Override
	public String getKeyName(){
		return "Variable";
	}

	@Override
	public String getNewDialogTitle(){
		return "Create new environment variable";
	}

	@Override
	protected String getValue( EnvironmentVariable entry ){
		return entry.getValue();
	}

	@Override
	public String getValueName(){
		return "Value";
	}	
}
