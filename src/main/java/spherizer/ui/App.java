package spherizer.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import spherizer.Distortion;
import spherizer.SimpleDistortion;

public class App implements ActionListener
{	
	private JFrame frame;
	private JProgressBar progress;
	private JCheckBoxMenuItem fitWindow;
	private JPanel content;
	private JMenuItem save;
	private BufferedImage distorted;
	
	public void launch() {
		frame = new JFrame("Spherizer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		progress = new JProgressBar();
		resetContentPane();
		createMenu();
		frame.setSize(300, 300);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private File chooseFile() {
		JFileChooser dialog = new JFileChooser();	
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
			"Pictures", "jpg", "jpeg", "gif", "png", "bmp");
		dialog.setFileFilter(filter);
		int result = dialog.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION) {
			return dialog.getSelectedFile();
		}
		
		return null;
	}
	
	private void processFile(File file) {
		try {
			final BufferedImage img = ImageIO.read(file);
			assert img != null; // TODO 
			final Distortion dist = new SimpleDistortion();
			Thread thread = new Thread(new Runnable() {
				@Override public void run() {
					distorted = dist.distort(img);
					SwingUtilities.invokeLater(new Runnable() {
						@Override public void run() {
							resetContentPane();
							progress.setIndeterminate(false);
							ImageIcon icon = new ImageIcon(distorted);
							content.add(new JScrollPane(new JLabel(icon)));
							frame.validate();
							if (fitWindow.isSelected()) {
								frame.pack();
							}
							save.setEnabled(true);
						}
					});
				}
			});
			progress.setIndeterminate(true);
			thread.start();
		}
		catch (Throwable t) {
			showErrorDialog(t, frame);
		}
	}
	
	private void saveImage() throws IOException {
		JFileChooser dialog = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG", "png");
		dialog.setFileFilter(filter);
		dialog.setSelectedFile(new File("distorted.png"));
		
		int result = dialog.showSaveDialog(frame);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			File f = dialog.getSelectedFile();	
			if (!f.getName().endsWith(".png")) {
				f = new File(f.getAbsolutePath() + ".png");
			}
			ImageIO.write(distorted, "png", f);
		}
	}
	
	private void createMenu() {
		JMenu file = new JMenu("File");
		file.add(createMenuItem("Open"));
		save = createMenuItem("Save", false);
		file.add(save);
		file.add(new JSeparator());
		fitWindow = new JCheckBoxMenuItem("Fit window to picture", true);
		file.add(fitWindow);
		file.add(new JSeparator());
		file.add(createMenuItem("Exit"));
		JMenuBar menu = new JMenuBar();
		menu.add(file);
		frame.setJMenuBar(menu);
	}
	
	private JMenuItem createMenuItem(String label) {
		return createMenuItem(label, true);
	}
	
	private JMenuItem createMenuItem(String label, boolean enabled) {
		JMenuItem item = new JMenuItem(label);
		item.addActionListener(this);
		item.setEnabled(enabled);
		return item;
	}
	
	private void resetContentPane() {
		content = (JPanel) frame.getContentPane();
		content.removeAll();
		content.setLayout(new BorderLayout());
		content.add(progress, BorderLayout.SOUTH);
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			new App().launch();
		}
		catch (Throwable t) {
			showErrorDialog(t, null);
		}
	}
	
	static private void showErrorDialog(Throwable t, Component parent) {
		showErrorDialog(t.getMessage(), parent);
	}
	
	static private void showErrorDialog(String message, Component parent) {
		JOptionPane.showMessageDialog(parent, message, 
			"Error", JOptionPane.ERROR_MESSAGE);
	}

 	public void actionPerformed(ActionEvent e) {
		final String cmd = e.getActionCommand();
		if (cmd.equals("Open")) {
			File f = chooseFile();
			if (f != null) {
				processFile(f);
			}
		}
		else if (cmd.equals("Exit")) {
			frame.setVisible(false);
			frame.dispose();
			System.exit(0);
		}
		else if (cmd.equals("Save")) {
			if (distorted == null) {
				showErrorDialog("no image loaded", frame);
			}
			try {
				saveImage();
			}
			catch (IOException ex) {
				showErrorDialog(ex, frame);
			}
		}
		else {
			assert false;
		}
	}
}
