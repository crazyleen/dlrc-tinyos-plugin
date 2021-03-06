package tinyos.dlrc.refactoring.entities.interfaces.alias.introduce;

import tinyos.dlrc.nesc12.parser.ast.nodes.general.Identifier;
import tinyos.dlrc.refactoring.abstractrefactoring.rename.RenameAvailabilityTester;
import tinyos.dlrc.refactoring.ast.AstAnalyzerFactory;
import tinyos.dlrc.refactoring.ast.ComponentAstAnalyzer;
import tinyos.dlrc.refactoring.entities.interfaces.rename.InterfaceSelectionIdentifier;
import tinyos.dlrc.refactoring.utilities.ASTUtil;

public class AvailabilityTester extends RenameAvailabilityTester{

	@Override
	protected boolean isSelectionAppropriate(Identifier selectedIdentifier) {
		AstAnalyzerFactory analyzerFactory=new AstAnalyzerFactory(selectedIdentifier);
		InterfaceSelectionIdentifier selectionIdentifier=new InterfaceSelectionIdentifier(selectedIdentifier,analyzerFactory);
		if(!selectionIdentifier.isInterfaceDeclaration()){
			return false;
		}
		ASTUtil astUtil=new ASTUtil();
		ComponentAstAnalyzer componentAnalyzer=analyzerFactory.getComponentAnalyzer();
		//If the interface is alredy aliased, the refactoring is not available. If it is not already aliased the local2global name map contains the identifier itself in its keyset.
		return astUtil.containsIdentifierInstance(selectedIdentifier, componentAnalyzer.getInterfaceLocalName2InterfaceGlobalName().keySet());
	}
	


}
