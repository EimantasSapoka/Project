import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


public class ResultController implements ActionListener{
	private ResultView viewObject;
	private ResultModel modelObject;

	public ResultController(ResultModel model) {
		modelObject = model;
	}

	public void setView(ResultView view) {
		viewObject = view;
	}

	

	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==viewObject.comboto || e.getSource()==viewObject.combofrom){
			int from=(Integer)viewObject.combofrom.getSelectedItem();
			int to=(Integer)viewObject.comboto.getSelectedItem();
			if (from==to) viewObject.moveproject.setEnabled(false);
			else  viewObject.moveproject.setEnabled(true);
			viewObject.refreshcomboproj(modelObject.allowedtransfer(from, to));
		}
		else if(e.getSource()==viewObject.moveproject){
			int from=(Integer)viewObject.combofrom.getSelectedItem();
			int to=(Integer)viewObject.comboto.getSelectedItem();
			if (viewObject.comboproj.getItemCount()>0)
			{int project=(Integer)viewObject.comboproj.getSelectedItem();
			modelObject.moveProject(from, to, project);
			viewObject.refreshcomboproj(modelObject.allowedtransfer(from, to));
			viewObject.refreshresult();
			viewObject.refreshdetails();
			}
		}
		else if (e.getSource()==viewObject.getOutput){
			try {
			   JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(viewObject);
				 if (returnVal == JFileChooser.APPROVE_OPTION) {
				modelObject.Output(fc.getSelectedFile());
				 }
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
		else if(e.getSource()==viewObject.projectreader){
			JFrame frame=new JFrame();
			frame.setSize(330, 500);
			frame.setTitle(modelObject.matchingtype+" matching");
			frame.setLayout(new FlowLayout());
			JPanel pan=new JPanel();
			HashMap<Integer,Integer> allocs=modelObject.getProjToReader();
			HashMap<Integer,Integer> ranks=modelObject.getProjToRank();
			pan.setLayout(new GridLayout(allocs.size()+1,1));
			for (int i=1;i<=PrefMain.projnum;i++){
				if (allocs.containsKey(i)) {
				JLabel resline=new JLabel(String.format("%-7d %-7d %d", i,allocs.get(i),ranks.get(i)));
				resline.setFont(new Font("Courier", Font.PLAIN, 14));
				pan.add(resline);
				}
			}
			JScrollPane scroll=new JScrollPane(pan);
			scroll.setPreferredSize(new Dimension(280,380));
			JPanel res=new JPanel();
			res.setBorder(new TitledBorder(new EtchedBorder(), "Project-------" +
					"Reader-------Rank"));
			res.add(scroll);
			frame.add(res);
			JButton getOutput=new JButton("Print to file");
			getOutput.addActionListener(this);
			frame.add(getOutput);
			frame.setVisible(true);
		}
		else {
			try {
			   JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(viewObject);
				 if (returnVal == JFileChooser.APPROVE_OPTION) {
				modelObject.OutputProjectReader(fc.getSelectedFile());
				 }
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		}
	}

}
