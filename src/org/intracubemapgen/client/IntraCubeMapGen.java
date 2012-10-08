package org.intracubemapgen.client;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Aaron McClure
 * IntraCube MapGen for development use
 */
public class IntraCubeMapGen extends JPanel implements MouseMotionListener, ChangeListener{

	private static final long serialVersionUID = -5041048902489539915L;
	public static void main(String[] args) {
		try {
			for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}catch (ClassNotFoundException ex){
		}catch (InstantiationException ex){
		}catch (IllegalAccessException ex){
		}catch (UnsupportedLookAndFeelException ex){
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						JPanel p = new IntraCubeMapGen();
						frame.setContentPane(p);
						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
						frame.setSize(800, 853);
						frame.setVisible(true);
					}
				});
			}
		});
	}


	public IntraCubeMapGen(){
		initComponents();
	}

	private void initComponents() {
		frame = new JFrame("IntraCube MapGen V1.0");		
		slideZoom = new JSlider();
		btnLocal = new JButton("Import Image Local");
		btnURL = new JButton("Import Image URL");
		lblZoom = new JLabel("Zoom: 1.0");
		lblPos = new JLabel("Pos: null");

		slideZoom.setPreferredSize(new Dimension(750,23));

		btnLocal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				importLocalAction(evt);
			}
		});

		btnURL.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				importURLAction(evt);
			}
		});

		this.canvas = new JPanel() {
			private static final long serialVersionUID = 1L;
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D)g;
				super.paintComponent(g2d);
				g2d.scale(zoom, zoom);
				g2d.drawImage(image, 0, 0, null);
			}
		};

		sPane = new JScrollPane(canvas);
		sPane.setPreferredSize(new Dimension(750,700));

		slideZoom.setMinimum(0);
		slideZoom.setMaximum(20);
		slideZoom.setMajorTickSpacing(1);
		slideZoom.setValue(1);
		slideZoom.setPaintTicks(true);

		add(sPane);
		add(lblZoom);
		add(lblPos);
		add(slideZoom);
		add(btnLocal);
		add(btnURL);

		sPane.addMouseMotionListener(this);
		slideZoom.addChangeListener(this);
	}

	private void importLocalAction(ActionEvent evt) {
		JFileChooser dialog = new JFileChooser();
		int openChoice = dialog.showOpenDialog(new JFrame());
		File selectedFile = null;
		if (openChoice == JFileChooser.APPROVE_OPTION){
			selectedFile = dialog.getSelectedFile();
		}
		try {
			image = ImageIO.read(selectedFile);
			JOptionPane.showMessageDialog(null, "Adjust zoom if image does not appear.");
			canvas.repaint();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "IO Exception.");
		} catch (IllegalArgumentException e2){
			// ignore
		}
	}

	private void importURLAction(ActionEvent evt) {
		String sUrl = JOptionPane.showInputDialog("Enter url.");
		if (sUrl == null || sUrl == "") return;
		try {
			image = Toolkit.getDefaultToolkit().createImage(new URL(sUrl));
			JOptionPane.showMessageDialog(null, "Adjust zoom if image does not appear.");
			canvas.repaint();
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(null, "Invalid URL.");
		}
	}

	private static JButton btnURL;
	private static JButton btnLocal;
	private static JLabel lblZoom, lblPos;
	private static JScrollPane sPane;
	private static JSlider slideZoom;
	private static JFrame frame;
	private static double zoom=1;

	private static Image image;
	private JPanel canvas;
	
	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		if (canvas.getMousePosition()!=null){
			lblPos.setText("Pos: " + "(" + canvas.getMousePosition().x + "," + canvas.getMousePosition().y + ")");
		}
	}
	
///////////////////////////
	
	@Override
	public void stateChanged(ChangeEvent e) {
		try{
		JSlider source = (JSlider)e.getSource();
		zoom = (source.getValue()>0) ? source.getValue() : 0.5;
		canvas.setPreferredSize(new Dimension((int)(image.getWidth(null)*zoom), (int)(image.getHeight(null)*zoom)));
		lblZoom.setText("Zoom: " + ((slideZoom.getValue()>0) ? slideZoom.getValue() : 0.5));

		canvas.invalidate();
		sPane.validate();
		sPane.repaint();
		}catch (NullPointerException ex){
			// ignore
		}
	}
}

