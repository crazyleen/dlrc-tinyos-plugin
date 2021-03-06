package tinyos.dlrc.refactoring.entities.field.rename.global;

import org.eclipse.core.commands.IHandler;

import tinyos.dlrc.refactoring.abstractrefactoring.rename.RenameActionHandler;
import tinyos.dlrc.refactoring.abstractrefactoring.rename.RenameInfo;
import tinyos.dlrc.refactoring.abstractrefactoring.rename.RenameProcessor;

public class ActionHandler extends RenameActionHandler implements IHandler{

	@Override
	protected RenameProcessor createProcessor(RenameInfo info) {
		return new GlobalFieldRenameProcessor(info);
	}

}
