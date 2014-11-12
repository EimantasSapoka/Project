import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


public class ResultView extends JFrame{
	
	private ResultController controllerObject;
	private ResultModel modelObject;
	private JPanel reslist,result,combos,details;
	private JLabel weight,profile;
	public JComboBox combofrom, comboto, comboproj;
	public JButton moveproject,projectreader,getOutput;
	
	public ResultView(ResultModel model, ResultController controller) {
		controllerObject = controller;
		modelObject = model;
		setSize(1024, 550);
		setTitle("Project Allocations - "+modelObject.matchingtype+" matching");
		setLayout(new FlowLayout());
		reslist=new JPanel();
		result=results(modelObject.getResults());
		reslist.add(result);
		this.add(reslist);
		this.add(moveproj());
		moveproject=new JButton("Reallocate project");
		moveproject.addActionListener(controllerObject);
		this.add(moveproject);
		projectreader=new JButton("Project-Reader view");
		projectreader.addActionListener(controllerObject);
		this.add(projectreader);
		getOutput=new JButton("Print to file");
		getOutput.addActionListener(controllerObject);
		this.add(getOutput);
		details=new JPanel();
		JLabel size=new JLabel("Size:"+modelObject.getSize());
		size.setFont(new Font("Courier", Font.BOLD, 14));
		details.add(size);
		weight=new JLabel("\t   Matching weight:"+modelObject.totalcost());
		weight.setFont(new Font("Courier", Font.BOLD, 14));
		profile=new JLabel("\t   Matching profile:"+ modelObject.profile());
		profile.setFont(new Font("Courier", Font.BOLD, 14));
		details.add(weight);
		details.add(profile);
		this.add(details);
	}
	
	
	
	private JPanel results(List<AllocationResult> list){
		JPanel pan=new JPanel();
		pan.setLayout(new GridLayout(list.size()+1,1));
		JLabel head=new JLabel(String.format("%-4s%-40s %-22s%-7s%-6s%-12s%-4s%-4s%-4s%s\n",
				"ID","Projects","Ranks","Target","Alloc","Cost(avg)","L3","L4","L5","MSc"));
		head.setFont(new Font("Courier", Font.PLAIN, 14));
		pan.add(head);
		for (AllocationResult res:list){
			JLabel resline=new JLabel(res.toString());
			resline.setFont(new Font("Courier", Font.PLAIN, 14));
			if (res.getTarget()> res.projnum()+ 1)
				resline.setForeground(Color.red);
		
			pan.add(resline);
		}
		JScrollPane scroll=new JScrollPane(pan);
		scroll.setPreferredSize(new Dimension(960,370));
		JPanel res=new JPanel();
		res.add(scroll);
		return res;
	}
	
	private JPanel moveproj(){
		combos=new JPanel();
		combos.add(new JLabel("Move a project from:"));
		combofrom=new JComboBox();
		comboto=new JComboBox();
		//comboto.addActionListener(controllerObject);
		for (AllocationResult a:modelObject.getResults()){
			combofrom.addItem(a.getId());
			comboto.addItem(a.getId());
		}
		combos.add(combofrom);
		combofrom.addActionListener(controllerObject);
		combos.add(new JLabel(" To:"));
		combos.add(comboto);
		comboto.addActionListener(controllerObject);
		comboproj=new JComboBox();
		combos.add(new JLabel(" Project Id:"));
		combos.add(comboproj);
		return combos;
	}
	
	public void refreshcomboproj(ArrayList<Integer> projlist){
		combos.remove(comboproj);
		comboproj=new JComboBox();
		for(Integer p:projlist) comboproj.addItem(p);
		combos.add(comboproj);
		combos.revalidate();
		combos.repaint();
	}
	
	public void refreshresult(){
		reslist.remove(result);
		result=results(modelObject.getResults());
		reslist.add(result);
		reslist.revalidate();
		reslist.repaint();
	}
	
	public void refreshdetails(){
		weight.setText("\t   Matching weight:"+modelObject.totalcost());
		profile.setText("\t   Matching profile:"+ modelObject.profile());
		weight.repaint();
		profile.repaint();
	}
	
}
