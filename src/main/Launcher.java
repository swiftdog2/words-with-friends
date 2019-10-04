package main;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ui.Board;
import util.Constants;
import words.WordTrie;

public class Launcher extends JFrame {
	private static final long serialVersionUID = 6951899007714995669L;
	private WordTrie trie;
	
	public Launcher(String title) {
		this.setTitle(title);
	}
	
	/*
	 * Loads the board UI
	 */
	public void loadUI() {
		//Set some basic properties
		setPreferredSize(new Dimension(280, 400));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		//Add the panel to the frame
		JPanel panel = new JPanel();
		panel.setPreferredSize(this.getPreferredSize());
		panel.setBackground(Constants.BOARD_BACKGROUND);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(250, 300));
		JList<GameInfo> list = new JList<GameInfo>();
		scrollPane.setViewportView(list);		
		panel.add(scrollPane);

		//GameLoader.getGames();
		
		//Add a button to load new 11x11 boards
		JButton board11Btn = new JButton("11 Board");
		board11Btn.setPreferredSize(new Dimension(100, 25));
		board11Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Fire up a new board
				Board b = new Board(Constants.BOARD_11);
				b.setTrie(trie);
				b.load();
			}
		});

		//Add a button to load new 15x15 boards
		JButton board15Btn = new JButton("15 Board");
		board15Btn.setPreferredSize(new Dimension(100, 25));
		board15Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//Fire up a new board
				Board b = new Board(Constants.BOARD_15);
				b.setTrie(trie);
				b.load();
			}
			
		});
		
		panel.add(board11Btn);
		panel.add(board15Btn);
		
		add(panel);
		
		//Pack the UI and fire it up
		pack();
		setVisible(true);
	}

	public void setDictionary(WordTrie trie) {
		this.trie = trie;
	}
}
