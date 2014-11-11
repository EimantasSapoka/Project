
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ReaderAllocController implements ActionListener{
	private ReaderAllocView viewObject;
	private ReaderAllocModel modelObject;
	private boolean alreadycomputed=false;

	public ReaderAllocController(ReaderAllocModel model) {
		modelObject = model;
	}

	public void setView(ReaderAllocView view) {
		viewObject = view;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==viewObject.extendButton) {
			alreadycomputed=false;
			int projId=(Integer)viewObject.comboId.getSelectedItem();
			if (viewObject.extL3Msc.isSelected()){
				modelObject.extend1(projId);
				viewObject.updateLists();
				viewObject.resetTextFields();
			}
			else if (viewObject.extNotBid.isSelected()){
				modelObject.extend2(projId);
				viewObject.updateLists();
				viewObject.resetTextFields();
			}
			else if (viewObject.extRandPerso.isSelected()){
				String projlist = viewObject.enterField.getText();
				modelObject.extend3(projId, projlist);
				viewObject.updateLists();
				viewObject.resetTextFields();
			}
			else if (viewObject.extPerso.isSelected()){
				String projlist = viewObject.enterField.getText();
				modelObject.extend4(projId, projlist);
				viewObject.updateLists();
				viewObject.resetTextFields();
			}

		}

		else if (e.getSource()==viewObject.extendshorts) {
			alreadycomputed=false;
			ArrayList<Integer> shortIds=new ArrayList<Integer>();
			if (viewObject.extL3Msc.isSelected()){
				modelObject.extendshorts1();
				viewObject.updateLists();
				viewObject.resetTextFields();
			}
			else if (viewObject.extNotBid.isSelected()){
				modelObject.extendshorts2();
				viewObject.updateLists();
				viewObject.resetTextFields();
			}
			else if (viewObject.extRandPerso.isSelected()){
				String projlist = viewObject.enterField.getText();
				modelObject.extendshorts3( projlist);
				viewObject.updateLists();
				viewObject.resetTextFields();
			}
			else if (viewObject.extPerso.isSelected()){
				for (PreferenceList p:modelObject.getShortlists())
					shortIds.add(p.getId());
				String projlist = viewObject.enterField.getText();
				for (Integer id:shortIds)
					modelObject.extend4(id, projlist);
				viewObject.updateLists();
				viewObject.resetTextFields();
			}


		}
		else if (e.getSource()==viewObject.resultButton) {
			String method="";
			if (viewObject.mincost.isSelected())
				method="Mincost maximum";
			else if (viewObject.greedy.isSelected())
				method="Greedy maximum";
			else if (viewObject.generous.isSelected())
				method="Generous maximum";
			else if (viewObject.greedygenerous.isSelected())
				method="Greedy generous";
			else if (viewObject.naivegreedy.isSelected())
				method="Naive greedy";
			try {
				if (!alreadycomputed){ 
					//to avoid recomputing the matching
				modelObject.runMatching();
				alreadycomputed=true;
				}
				modelObject.showRes(method);
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}

	}

}
