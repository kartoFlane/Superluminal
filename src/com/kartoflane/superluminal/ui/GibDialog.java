package com.kartoflane.superluminal.ui;

import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.kartoflane.superluminal.core.Main;
import com.kartoflane.superluminal.core.ShipIO;
import com.kartoflane.superluminal.elements.FTLGib;
import com.kartoflane.superluminal.painter.LayeredPainter;

public class GibDialog {
	protected Shell shell;
	public List list;
	private Button btnMoveUp;
	private Button btnMoveDown;
	private Button btnAddGib;
	private Button btnDeleteGib;
	private Button btnHideGib;
	public HashSet<String> letters = new HashSet<String>();
	private Button btnAnimate;
	public boolean animating = false;
	private String dialog_path = Main.resPath + ShipIO.pathDelimiter + "img" + ShipIO.pathDelimiter + "ship";
	
	public GibDialog(Shell shl) {
		shell = new Shell(shl, SWT.BORDER | SWT.TITLE);
		
		createContents();
		
		shell.setText(Main.APPNAME + " - Gibs Editor");
		
		shell.pack();
		
		btnMoveUp.setEnabled(false);
		btnMoveDown.setEnabled(false);
		btnDeleteGib.setEnabled(false);
		btnHideGib.setEnabled(false);
		
		shell.setLocation(Main.shell.getBounds().x + Main.shell.getBounds().width - shell.getBounds().width - 20, Main.shell.getBounds().y + 100);
	}
	
	public void setVisible(boolean vis) {
		if (vis) {
			shell.open();
			Main.shell.setActive();
		}
		shell.setVisible(vis);
	}
	
	public boolean isVisible() {
		return shell.isVisible();
	}
	
	public void clearList() {
		list.removeAll();
	}

	protected void createContents() {
		shell.setLayout(new GridLayout(2, false));
		list = new List(shell, SWT.BORDER | SWT.V_SCROLL);
		GridData gd_list = new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1);
		gd_list.widthHint = 115;
		list.setLayoutData(gd_list);
		list.setFont(Main.appFont);
		
		Composite composite = new Composite(shell, SWT.NONE);
		GridLayout gl_composite = new GridLayout(1, false);
		composite.setLayout(gl_composite);
		
		btnMoveUp = new Button(composite, SWT.NONE);
		btnMoveUp.setFont(Main.appFont);
		btnMoveUp.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnMoveUp.setText("Move Up");
		
		btnMoveDown = new Button(composite, SWT.NONE);
		btnMoveDown.setFont(Main.appFont);
		btnMoveDown.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnMoveDown.setText("Move Down");
		
		Label label = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		
		btnHideGib = new Button(composite, SWT.NONE);
		btnHideGib.setFont(Main.appFont);
		btnHideGib.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnHideGib.setText("Hide Gib");
		
		btnAddGib = new Button(composite, SWT.NONE);
		btnAddGib.setFont(Main.appFont);
		btnAddGib.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		btnAddGib.setText("Add Gib");
		
		btnDeleteGib = new Button(composite, SWT.NONE);
		btnDeleteGib.setFont(Main.appFont);
		btnDeleteGib.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
		btnDeleteGib.setText("Delete Gib");
		
		btnAnimate = new Button(shell, SWT.NONE);
		btnAnimate.setEnabled(false);
		btnAnimate.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnAnimate.setText("Animate");
		
		list.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (list.getSelection() != null && list.getSelection().length != 0) {
					String s = list.getSelection()[0];
					for (FTLGib gb : Main.ship.gibs) {
						if (gb.ID.equals(s.substring(7,8))) {
							gb.select();
							gb.move = false;
							if (!gb.isVisible()) {
								btnHideGib.setText("Show Gib");
							} else {
								btnHideGib.setText("Hide Gib");
							}
						} else {
							gb.deselect();
						}
					}
					
					select(list.getSelectionIndex());
				}
			}
		});
		
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				if (Main.selectedGib!=null && !Main.gibWindow.isVisible()) Main.gibWindow.open();
			}
		});
		
		btnMoveUp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveSelectedUp();
			}
		});
		
		btnMoveDown.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				moveSelectedDown();
			}
		});
		
		btnHideGib.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				hideSelected(Main.ship.gibs.get(list.getSelectionIndex()).isVisible());
			}
		});
		
		btnAddGib.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				String[] filterExtensions = new String[] {"*.png"};
				dialog.setFilterExtensions(filterExtensions);
				dialog.setFilterPath(dialog_path);
				String path = dialog.open();
				dialog_path = new String(path);
				
				if (!ShipIO.isNull(path)) {
					FTLGib g = new FTLGib();
					g.number = Main.ship.gibs.size() + 1;
					g.setImage(path, false);
					
					g.add(Main.ship);
					g.setLocationRelative(0, 0);
					Main.canvasRedraw(g.getBounds(), false);
				}
			}
		});
		
		btnDeleteGib.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FTLGib g = Main.ship.gibs.get(list.getSelectionIndex());
				if (Main.gibWindow.isVisible()) Main.gibWindow.escape();
				list.remove(list.getSelectionIndex());
				g.dispose();
				Main.ship.gibs.remove(g);
				letters.remove(g.ID);
				Main.canvas.redraw(g.getBounds().x-1, g.getBounds().y-1, g.getBounds().width+2, g.getBounds().height+2, false);
				btnDeleteGib.setEnabled(false);
				
				refreshList();
			}
		});
		
		shell.addListener(SWT.Traverse, new Listener() {
			@Override
			public void handleEvent(Event e) {
				if (e.detail == SWT.TRAVERSE_ESCAPE) {
					e.doit = false;
				}
			}
		});

		shell.getDisplay().addFilter(SWT.KeyDown, new Listener() {
			public void handleEvent(Event e) {
				if (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.ARROW_LEFT || e.keyCode == SWT.ARROW_RIGHT) {
					e.doit = e.stateMask == SWT.CTRL;
				}
			}
		});
	}
	
	private void hideSelected(boolean hide) {
		FTLGib g = Main.ship.gibs.get(list.getSelectionIndex());
		g.setVisible(!hide);
		Main.canvasRedraw(g.getBounds(), false);
		if (hide) {
			list.setItem(list.getSelectionIndex(), list.getSelection()[0] + " - hidden");
			btnHideGib.setText("Show Gib");
		} else {
			list.setItem(list.getSelectionIndex(), list.getSelection()[0].replace(" - hidden", ""));
			btnHideGib.setText("Hide Gib");
		}
	}
	
	private void moveSelectedUp() {
		FTLGib g = Main.ship.gibs.get(list.getSelectionIndex());
		Main.ship.gibs.remove(g);
		Main.ship.gibs.add(g.number-2, g);
		
		Main.layeredPainter.getLayerMap().get(LayeredPainter.GIB).clear();
		
		clearList();
		int i=0;
		for (FTLGib gb : Main.ship.gibs) {
			i++;
			gb.number = i;
			Main.gibDialog.list.add(gb.number + ". gib " + gb.ID + (gb.isVisible() ? "" : " - hidden"));
			Main.layeredPainter.addAsFirst(gb, LayeredPainter.GIB);
		}
		
		Main.canvasRedraw(g.getBounds(), false);
		
		list.select(g.number-1);
		btnMoveUp.setEnabled(g.number>1);
		btnMoveDown.setEnabled(g.number<list.getItemCount());
	}
	
	private void moveSelectedDown() {
		FTLGib g = Main.ship.gibs.get(list.getSelectionIndex());
		Main.ship.gibs.remove(g);
		Main.ship.gibs.add(g.number, g);
		
		Main.layeredPainter.getLayerMap().get(LayeredPainter.GIB).clear();
		
		clearList();
		int i=0;
		for (FTLGib gb : Main.ship.gibs) {
			i++;
			gb.number = i;
			Main.gibDialog.list.add(gb.number + ". gib " + gb.ID);
			Main.layeredPainter.addAsFirst(gb, LayeredPainter.GIB);
		}

		Main.canvasRedraw(g.getBounds(), false);
		
		list.select(g.number-1);
		btnMoveUp.setEnabled(g.number>1);
		btnMoveDown.setEnabled(g.number<list.getItemCount());
	}
	
	public void refreshList() {
		clearList();
		int i = 0;
		for (FTLGib gb : Main.ship.gibs) {
			i++;
			gb.number = i;
			Main.gibDialog.list.add(gb.number + ". gib " + gb.ID);
		}

	}
	
	public void select(int index) {
		if (index != -1) {
			list.select(index);
			
			if (list.getSelection() != null && list.getSelection().length != 0) {
				btnMoveUp.setEnabled(index!=0);
				btnMoveDown.setEnabled(index+1!=list.getItemCount());
				btnDeleteGib.setEnabled(!ShipIO.isNull(list.getSelection()[0]));
				btnHideGib.setEnabled(!ShipIO.isNull(list.getSelection()[0]));
			}
		} else {
			list.deselectAll();
			btnMoveUp.setEnabled(false);
			btnMoveDown.setEnabled(false);
			btnDeleteGib.setEnabled(false);
			btnHideGib.setEnabled(false);
		}
	}
}
