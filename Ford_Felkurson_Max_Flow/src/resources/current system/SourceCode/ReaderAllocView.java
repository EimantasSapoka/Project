import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


public class ReaderAllocView extends JFrame
{  
	public JButton extendButton,extendshorts, resultButton;
	public JTextField enterField;
	public JRadioButton extL3Msc, extNotBid, extRandPerso, extPerso, 
	mincost,greedy,generous,greedygenerous,naivegreedy;
	public JComboBox comboId;
	private JPanel lists;
	private JLabel notBidFor;
	private ReaderAllocController controllerObject;
	private ReaderAllocModel modelObject;
	private JPanel shortLists,allLists;

	public ReaderAllocView(ReaderAllocModel model, ReaderAllocController controller) {
		controllerObject = controller;
		modelObject = model;
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1024, 570);
		setTitle("Allocating Readers to Projects");
		setLayout(new GridLayout(2,1));
		//setLayout(new FlowLayout());
		this.add(centerPan(),BorderLayout.CENTER);
		this.add(southPan(),BorderLayout.SOUTH);
	}

	private JPanel centerPan(){
		JPanel panel=new JPanel();
		lists=new JPanel();
		//lists.setLayout(new GridLayout(1,2));
		shortLists=ListPan("Short lists",modelObject.getShortlists());
		allLists=ListPan("All lists",modelObject.getPreferences());
		lists.add(shortLists);
		lists.add(allLists);
		panel.add(lists);
		return panel;
	}
	
	private JPanel southPan(){
		JPanel res=new JPanel();
		//res.setLayout(new BorderLayout());
		res.add(notBidPan(),BorderLayout.NORTH);
		JPanel panel=new JPanel();
		panel.setLayout(new GridLayout(1,3));
		panel.setSize(300,200);
		panel.setBorder(new TitledBorder(new EtchedBorder(), "Extend a list"));
		JPanel choselist=new JPanel();
		choselist.add(new JLabel("List to extend : "));
		comboId=new JComboBox();
		for (PreferenceList p : modelObject.getPreferences())
			comboId.addItem(p.getId());
		choselist.add(comboId);
		JPanel method=new JPanel();
		method.setLayout(new GridLayout(4,1));
		ButtonGroup howto=new ButtonGroup();
		extL3Msc=new JRadioButton("With a random subset of L3 and Msc");
		extL3Msc.setSelected(true);
		howto.add(extL3Msc);
		method.add(extL3Msc);
		extNotBid=new JRadioButton("With a random subset of \"not bid for\"");
		howto.add(extNotBid);
		method.add(extNotBid);
		extRandPerso=new JRadioButton("With a random subset of a set of your choice");
		howto.add(extRandPerso);
		method.add(extRandPerso);
		extPerso=new JRadioButton("With a list of your choice");
		howto.add(extPerso);
		method.add(extPerso);
		panel.add(method);
		JPanel center=new JPanel();
		center.setLayout(new GridLayout(3,1));
		center.add(choselist);
		JLabel writehere=new JLabel("IDs of projects to add: ");
		enterField= new JTextField();
		center.add(writehere);
		center.add(enterField);
		panel.add(center);
		extendButton=new JButton("Extend");
		extendshorts=new JButton("Extend all 'shorts'");
		extendButton.addActionListener(controllerObject);
		extendshorts.addActionListener(controllerObject);
		JPanel extbuttons=new JPanel();
		extbuttons.add(extendButton);
		extbuttons.add(extendshorts);
		panel.add(extbuttons);
		
		res.add(panel);
		
		JPanel buttons=new JPanel();
		ButtonGroup matchalgo=new ButtonGroup();
		mincost=new JRadioButton("Mincost");
		mincost.setSelected(true);
		matchalgo.add(mincost);
		buttons.add(mincost);
		greedy=new JRadioButton("Greedy");
		matchalgo.add(greedy);
		buttons.add(greedy);
		generous=new JRadioButton("Generous");
		matchalgo.add(generous);
		buttons.add(generous);
		greedygenerous=new JRadioButton("Greedy generous");
		matchalgo.add(greedygenerous);
		buttons.add(greedygenerous);
		naivegreedy=new JRadioButton("Naive greedy");
		matchalgo.add(naivegreedy);
		buttons.add(naivegreedy);
		resultButton=new JButton("Results");
		resultButton.addActionListener(controllerObject);
		buttons.add(resultButton);
		res.add(buttons,BorderLayout.CENTER);
		return res;
	}
	
	private JPanel ListPan(String title,List<PreferenceList> list){
		JPanel pan=new JPanel();
		pan.setBorder(new TitledBorder(new EtchedBorder(), title));
		Vector<Vector<?>> listitems=new Vector<Vector<?>>();
		Vector<String> header=new Vector<String>();
		header.add("ID");
		header.add("Supervised");
		header.add("Target");
		header.add("Preferences");
		JTable slist=new JTable();
		JScrollPane scrollist;
		for (PreferenceList p : list)
			listitems.add(p.toVect());
		slist=new JTable(listitems,header);
		slist.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		slist.getColumnModel().getColumn(0).setPreferredWidth(35);
		slist.getColumnModel().getColumn(1).setPreferredWidth(175);
		slist.getColumnModel().getColumn(2).setPreferredWidth(50);
		slist.getColumnModel().getColumn(3).setPreferredWidth(330);
		slist.setEnabled(false);
		scrollist=new JScrollPane(slist);
		scrollist.setPreferredSize(new Dimension(470,220));
		pan.add(scrollist);
		return pan;
	}
	
	private JPanel notBidPan(){
		JPanel up=new JPanel();
		up.setBorder(new TitledBorder(new EtchedBorder(),"Projects not bid for by anybody"));
		up.setPreferredSize(new Dimension(800,50));
		notBidFor=new JLabel(modelObject.notbid());
		up.add(notBidFor);
		return up;
	}

	
	public void resetTextFields() {
		enterField.setText("");
	}

	public void updateLists(){
		lists.remove(shortLists);
		lists.remove(allLists);	
		shortLists=ListPan("Short lists",modelObject.getShortlists());
		allLists=ListPan("All lists",modelObject.getPreferences());
		lists.add(shortLists);
		lists.add(allLists);	
	     lists.revalidate();	
	     lists.repaint();	
		notBidFor.setText(modelObject.notbid());
		notBidFor.repaint();
		
	}
	
	
}

