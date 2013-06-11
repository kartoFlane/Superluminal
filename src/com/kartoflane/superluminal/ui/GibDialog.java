package com.kartoflane.superluminal.ui;

import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
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
import com.kartoflane.superluminal.elements.FTLMount;
import com.kartoflane.superluminal.painter.LayeredPainter;

public class GibDialog {
	protected Shell shell;
	public List list;
	private Button btnMoveUp;
	private Button btnMoveDown;
	private Button btnAddGib;
	private Button btnDeleteGib;
	public Button btnHideGib;
	public HashSet<String> letters = new HashSet<String>();
	public Button btnAnimate;
	public boolean animating = false;
	private String dialog_path = Main.resPath + ShipIO.pathDelimiter + "img" + ShipIO.pathDelimiter + "ship";
	public Point relativePosition = new Point(0,0);
	public boolean autoReposition = false;
	
	public boolean mainMoved = true;
	
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
		relativePosition.x = shell.getLocation().x - Main.shell.getLocation().x;
		relativePosition.y = shell.getLocation().y - Main.shell.getLocation().y;
	}
	
	public void setVisible(boolean vis) {
		if (vis) {
			shell.open();
			Main.shell.setActive();
		}
		shell.setVisible(vis);
		
		if (mainMoved) {
			shell.setLocation(Main.canvas.toDisplay(Main.canvas.getSize().x-shell.getSize().x,7));
			mainMoved = false;
		}
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
				if (Main.selectedGib!=null && !Main.gibWindow.isVisible() && !Main.animateGibs) Main.gibWindow.open();
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
				dialog.setFileName(dialog_path);
				String path = dialog.open();
				
				if (!ShipIO.isNull(path)) {
					dialog_path = new String(path);
					
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
				if (Main.tltmGib.getSelection() && (e.keyCode == SWT.ARROW_UP || e.keyCode == SWT.ARROW_DOWN || e.keyCode == SWT.ARROW_LEFT || e.keyCode == SWT.ARROW_RIGHT)) {
					e.doit = e.stateMask == SWT.CTRL;
				}
			}
		});
		
		shell.addControlListener(new ControlListener() {
			@Override
			public void controlMoved(ControlEvent e) {
				if (!autoReposition) {
					relativePosition.x = shell.getLocation().x - Main.shell.getLocation().x;
					relativePosition.y = shell.getLocation().y - Main.shell.getLocation().y;
				}
			}

			@Override
			public void controlResized(ControlEvent e) {}
		});
		
		btnAnimate.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!Main.animateGibs) {
					Main.animateGibs = true;
					for (FTLGib g : Main.ship.gibs)
						g.setAnimationValues();
					for (FTLMount m : Main.ship.mounts) {
						m.setVisible(false);
						/*
						if (m.gib==0) {
							m.setVisible(false);
							continue;
						}
						m.animX = m.getBounds().x;
						m.animY = m.getBounds().y;
						m.animPos.x = m.getLocation().x;
						m.animPos.y = m.getLocation().y;
						//m.animDist = Math.sqrt(Math.pow((m.animX - Main.ship.gibs.get(m.gib-1).getBounds().x), 2)+Math.pow((m.animY - Main.ship.gibs.get(m.gib-1).getBounds().y), 2));
						*/
					}
					
					Main.shell.setEnabled(false);
					Main.gibWindow.escape();
					
					enableButtons(false);
				} else {
					Main.animateGibs = false;
					Main.timeElapsed = 0;
					Main.shell.setEnabled(true);
					enableButtons(true);
					for (FTLGib g : Main.ship.gibs) {
						g.setLocationRelative(g.position.x, g.position.y);
						g.setRotation(0);
					}
					for (FTLMount m : Main.ship.mounts) {
						/*
						m.setLocation(m.animPos.x, m.animPos.y);
						m.setRotation((int) Math.round(m.isRotated() ? 90 : 0));
						if (m.gib==0) m.setVisible(true);
						*/
						m.setVisible(true);
					}
					
					Main.canvas.redraw();
				}
				
			}
		});
	}
	
	public void enableButtons(boolean enable) {
		//btnAnimate.setEnabled(enable);
		btnAnimate.setText(enable ? "Animate" : "Stop");
		btnAddGib.setEnabled(enable);
		if (Main.selectedGib!=null)
			Main.selectedGib.deselect();
		list.setEnabled(enable);
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
	
	public Point getLocation() {
		return shell.getLocation();
	}
	
	public void setLocation(int x, int y) {
		shell.setLocation(x, y);
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
