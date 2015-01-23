
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;



public class PrefMain implements ActionListener{

	public static int lastl3;
	public static int lastl4;
	public static int lastl5;
	public static int projnum; //number of projects
	private static JFrame params;
	private static JTextField t1;
	private static JTextField t2;
	private static JTextField t3;
	private static JTextField t4;
	private static JTextField t5;
	private static JButton go;
	private JButton choosefile;
	private JFileChooser fc;
	private FileInputStream f=null;


	public void actionPerformed(ActionEvent evt) {
		boolean fileok=true, paramok=true;
		
		if (evt.getSource() == choosefile) {
			int returnVal = fc.showOpenDialog(params);
			 if (returnVal == JFileChooser.APPROVE_OPTION) {
	                File file = fc.getSelectedFile();
	                t1.setText(file.getName());
	                try {
						f=new FileInputStream(file);
					} catch (FileNotFoundException e) {
						fileok=false;
						e.printStackTrace();
					}
			 }
		}
		else
		{
			
			try{
				projnum=Integer.parseInt(t2.getText());
				lastl3=Integer.parseInt(t3.getText());
				lastl4=Integer.parseInt(t4.getText());
				lastl5=Integer.parseInt(t5.getText());
			}catch (NumberFormatException e) {
				paramok=false;
			}
			try{if (f==null)
				f=new FileInputStream(t1.getText());
			}catch (Exception e) {
				fileok=false;
			}
			//preconditions: projects are ordered, with at least one in each level
			paramok=paramok && lastl3>0 && lastl3<lastl4 
			&& lastl4<lastl5 && lastl5<projnum;
			if (!fileok)
				JOptionPane.showMessageDialog(params,t1.getText()+" : File not found!",
						" File not found!",JOptionPane.ERROR_MESSAGE);
			else if (! paramok)
				JOptionPane.showMessageDialog(params,"Incorrect parameters inserted",
						"Error!",JOptionPane.ERROR_MESSAGE);
			else {
				params.setVisible(false);
				ReaderAllocModel model = new ReaderAllocModel(f);
				ReaderAllocController controller = new ReaderAllocController(model);
				ReaderAllocView view = new ReaderAllocView(model, controller);
				controller.setView(view);
				view.setVisible(true);
			}
		}
	}



	public PrefMain(){
		fc = new JFileChooser();
		params=new JFrame();
		params.setTitle("Set Parameters");
		params.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		params.setSize(350, 250);
		params.setLocation(350, 200);
		params.setLayout(new GridLayout(6,1));
		JPanel line1=new JPanel();
		line1.add(new JLabel("Input file:"));
		t1=new JTextField();
		t1.setPreferredSize(new Dimension(170,20));
		choosefile=new JButton("Browse");
		choosefile.addActionListener(this);
		line1.add(t1);
		line1.add(choosefile);
		params.add(line1);
		JPanel line2=new JPanel();
		line2.add(new JLabel("Number of projects :"));
		t2=new JTextField();
		t2.setPreferredSize(new Dimension(50,20));
		line2.add(t2);
		params.add(line2);
		JPanel line3=new JPanel();
		line3.add(new JLabel("Last index of L3 project :"));
		t3=new JTextField();
		t3.setPreferredSize(new Dimension(50,20));
		line3.add(t3);
		params.add(line3);
		JPanel line4=new JPanel();
		line4.add(new JLabel("Last index of L4 project :"));
		t4=new JTextField();
		t4.setPreferredSize(new Dimension(50,20));
		line4.add(t4);
		params.add(line4);
		JPanel line5=new JPanel();
		line5.add(new JLabel("Last index of L5 project :"));
		t5=new JTextField();
		t5.setPreferredSize(new Dimension(50,20));
		line5.add(t5);
		params.add(line5);
		go=new JButton("GO");
		go.addActionListener(this);
		params.add(go);
		params.setVisible(true);
	}



	public static void main(String[] args) {
		new PrefMain();
	}




}
