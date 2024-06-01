package com.malone.dbms.desktopSwing.views;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.DefaultTreeCellRenderer;

import java.awt.*;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

public class AppFrame extends JFrame {
    private JMenuBar menuBar = new JMenuBar();

	public JMenuItem newDbMenuItem = new JMenuItem("New DataBase", 'N');
    public JMenuItem selectDbMenuItem = new JMenuItem("Select DataBase", 'S');
    public JMenuItem dropDbMenuItem = new JMenuItem("Drop DataBase", 'D');

    public JMenuItem selectTableMenuItem = new JMenuItem("Select Table", 'S');
    public JMenuItem insertTableMenuItem = new JMenuItem("Insert Into Table", 'I');
    public JMenuItem updateTableMenuItem = new JMenuItem("Update VALUES", 'U');
    public JMenuItem deleteRowMenuItem = new JMenuItem("Delete Row", 'R');
    public JMenuItem newTableMenuItem = new JMenuItem("New Table", 'T');
    public JMenuItem dropTableMenuItem = new JMenuItem("Drop Table", 'D');

    public JComboBox databaseComboBox = new JComboBox<>();

    private JPanel mainContentPanel = new JPanel(new BorderLayout(10, 10));
    private JPanel sideContentPanel = new JPanel(new BorderLayout(1, 1));

    public JButton refreshButton = new JButton("Refresh");

    public JTree databaseTree;
    public TreeSelectionModel selectionModel;

	public AppFrame() {
		super("Malonza DBMS");
        this.init();
        // this.setVisible(true);
	}

    public AppFrame(String title) {
        super(title);
        this.init();
        // this.setVisible(true);
    }

    private void init() {
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setSize(1450, 730);
        this.setFont(new Font("Dialog", Font.PLAIN,18));

        // menu bar
        initMenuBar();
        this.setJMenuBar(this.menuBar);
        
        // set Frame layout
        this.setLayout(new BorderLayout());
        this.add(this.headerPanel(), BorderLayout.NORTH);
        this.add(this.mainContentPanel, BorderLayout.CENTER);
        this.add(this.sideContentPanel, BorderLayout.WEST);
        // this.add(buttonPanel, BorderLayout.SOUTH);

    }

	private void initMenuBar() {

		JMenu databaseMenu = new JMenu("Database");
        databaseMenu.setMnemonic((int)'D');
        databaseMenu.add(newDbMenuItem);
        databaseMenu.add(selectDbMenuItem);
        databaseMenu.add(dropDbMenuItem);

        JMenu tableMenu = new JMenu("Table");
        tableMenu.setMnemonic((int)'T');
        tableMenu.add(selectTableMenuItem);
        tableMenu.addSeparator();
        tableMenu.add(insertTableMenuItem);
        tableMenu.add(updateTableMenuItem);
        tableMenu.add(deleteRowMenuItem);
        tableMenu.addSeparator();
        tableMenu.add(newTableMenuItem);
        tableMenu.add(dropTableMenuItem);

        this.menuBar.add(databaseMenu);
        this.menuBar.add(tableMenu);
	}

	private JPanel headerPanel() {
		JPanel headerPanel = new JPanel(new BorderLayout(10, 10));

        JLabel headerLabel = new JLabel("MALONE DBMS");
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel comboLabel = new JLabel("USE DATABASE: ");
        comboLabel.setHorizontalAlignment(SwingConstants.TRAILING);

		this.databaseComboBox.addItem("<none configured>");
		this.databaseComboBox.addItem("");
        // this.databaseComboBox.setFont(new Font("verdana", Font.PLAIN, 12));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        panel.add(this.databaseComboBox);

        JPanel comboPanel = new JPanel(new GridLayout(1,4));
        comboPanel.add(new JLabel());
        comboPanel.add(comboLabel);
        comboPanel.add(panel);
        comboPanel.add(new JLabel());

        headerPanel.add(headerLabel, BorderLayout.CENTER);
        headerPanel.add(comboPanel, BorderLayout.SOUTH);

        return headerPanel;
	}

	public void setMainContentPanel(JInternalFrame frame) {
		this.mainContentPanel.removeAll();
        this.mainContentPanel.repaint();
        this.mainContentPanel.revalidate();
		this.mainContentPanel.add(frame, BorderLayout.CENTER);
		frame.setVisible(true);
	}

	public void setDatabaseTree(Hashtable<String, Vector> dataMap) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Databases");

		// Enumeration <String> data = dataMap.keys(); // get keys
		// int dataSize = dataMap.size();

        for (Enumeration <String> e = dataMap.keys(); e.hasMoreElements();) {
            String key = e.nextElement();
        	DefaultMutableTreeNode databaseNode = new DefaultMutableTreeNode(key);

            Vector tables = dataMap.get(key);

            int tableNum = 0;

            for (Object table : tables) {
                DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(table.toString());
                databaseNode.insert(tableNode, tableNum);
                tableNum++;
            }

            root.insert(databaseNode, 0);
        }

        this.databaseTree = new JTree(root);
        this.selectionModel = this.databaseTree.getSelectionModel();
        this.selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(databaseTree);

        JLabel label = new JLabel("Navigate Databases");
        label.setPreferredSize(new Dimension(180, 30));
        // label.setFont(new Font("verdana", Font.PLAIN, 14));

        this.refreshButton.setPreferredSize(new Dimension(80, 30));
        // this.refreshButton.setFont(new Font("verdana", Font.PLAIN, 14));

        this.sideContentPanel.removeAll();
        this.sideContentPanel.repaint();
        this.sideContentPanel.revalidate();
        
        this.sideContentPanel.add(scrollPane, BorderLayout.CENTER);
        this.sideContentPanel.add(label, BorderLayout.NORTH);
        this.sideContentPanel.add(this.refreshButton, BorderLayout.SOUTH);
	}

    public void setDatabaseComboBox(Vector<String> items) {
        this.databaseComboBox.removeAllItems();
        for (int i = 0; i < items.size(); i++) {
            this.databaseComboBox.addItem(items.get(i).toString());
        }
    }

}

