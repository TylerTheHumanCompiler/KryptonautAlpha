/*
 * License GNU LGPL
 * Copyright (C) 2013 Amrullah <amrullah@panemu.com>.
 */
package javafx_finalchat;

import com.panemu.tiwulfx.common.TiwulFXUtil;
import com.panemu.tiwulfx.control.DetachableTab;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author amrullah
 */
public class DetachableTabPane extends TabPane {

	/**
	 * hold reference to the source of drag event. We can't use event.getGestureSource() because it is null when the
	 * target on a different stage
	 */
	private static DetachableTabPane DRAG_SOURCE;
	private StringProperty scope = new SimpleStringProperty("");
	
	public DetachableTabPane() {
		super();
		attachListeners();

	}

	/**
	 * Get drag scope id
	 *
	 * @return
	 */
	public String getScope() {
		return scope.get();
	}

	/**
	 * Set scope id. Only TabPane having the same scope that could be drop target. Default is empty string. So the
	 * default behavior is this TabPane could receive tab from empty scope DragAwareTabPane
	 *
	 * @param scope
	 */
	public void setScope(String scope) {
		this.scope.set(scope);
	}

	/**
	 * Scope property. Only TabPane having the same scope that could be drop target.
	 *
	 * @return
	 */
	public StringProperty scopeProperty() {
		return scope;
	}

	private void attachListeners() {
		/**
		 * This listener detects when the TabPane is shown. Then it will call initiateDragGesture. It because the
		 * lookupAll call in that method only works if the stage containing this instance is already shown.
		 */
		sceneProperty().addListener(new ChangeListener<Scene>() {
			@Override
			public void changed(ObservableValue<? extends Scene> ov, Scene t, Scene t1) {
				if (t == null && t1 != null) {
					if (getScene().getWindow() != null) {
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								initiateDragGesture();
							}
						});
					} else {
						getScene().windowProperty().addListener(new ChangeListener<Window>() {
							@Override
							public void changed(ObservableValue<? extends Window> ov, Window t, Window t1) {
								if (t == null && t1 != null) {
									t1.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>() {
										@Override
										public void handle(WindowEvent t) {
											initiateDragGesture();
										}
									});
								}
							}
						});
					}
				}
			}
		});


		this.addEventHandler(DragEvent.ANY, new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent event) {
				if (DRAG_SOURCE == null) {
					return;
				}
				if (event.getEventType() == DragEvent.DRAG_OVER) {
					Dragboard db = event.getDragboard();

					if (DetachableTabPane.this.scope.get().equals(DRAG_SOURCE.getScope())) {
						event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
					}
					event.consume();
				} else if (event.getEventType() == DragEvent.DRAG_DROPPED) {
					if (DRAG_SOURCE != null && DRAG_SOURCE != DetachableTabPane.this) {
						final Tab selectedtab = DRAG_SOURCE.getSelectionModel().getSelectedItem();
						DRAG_SOURCE.getTabs().remove(selectedtab);
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								DetachableTabPane.this.getTabs().add(selectedtab);
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										DetachableTabPane.this.getSelectionModel().select(selectedtab);
									}
								});
							}
						});

						event.setDropCompleted(true);
					} else {
						event.setDropCompleted(DRAG_SOURCE == DetachableTabPane.this);
					}
					event.consume();
				}
			}
		});

		getTabs().addListener(new ListChangeListener<Tab>() {
			@Override
			public void onChanged(ListChangeListener.Change<? extends Tab> change) {
				while (change.next()) {
					if (change.wasAdded()) {
						if (getScene() != null && getScene().getWindow() != null) {
							if (getScene().getWindow().isShowing()) {
								Platform.runLater(new Runnable() {
									@Override
									public void run() {
										clearGesture();
										initiateDragGesture();
									}
								});
							}
						}
					}
				}
			}
		});
	}

	/**
	 * The lookupAll call in this method only works if the stage containing this instance is already shown.
	 */
	private void initiateDragGesture() {
		Set<Node> tabs = this.lookupAll(".tab");
		for (Node node : tabs) {
			addGesture(this, node);
		}

	}

	private void clearGesture() {
		Set<Node> tabs = this.lookupAll(".tab");
		for (Node node : tabs) {
			node.setOnDragDetected(null);
			node.setOnDragDone(null);
		}
	}
	private static final DataFormat dataFormat = new DataFormat("dragAwareTab");

	private void addGesture(final TabPane tabPane, final Node node) {
		node.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				Tab tab = tabPane.getSelectionModel().getSelectedItem();
				if (tab instanceof DetachableTab && !((DetachableTab) tab).isDetachable()) {
					return;
				}
				Dragboard db = node.startDragAndDrop(TransferMode.ANY);
				Map<DataFormat, Object> dragContent = new HashMap<>();
				dragContent.put(dataFormat, "test");
				DetachableTabPane.DRAG_SOURCE = DetachableTabPane.this;
				db.setContent(dragContent);
				e.consume();
			}
		});

		node.setOnDragDone(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				if (!event.isAccepted()) {
					Tab tab = tabPane.getSelectionModel().getSelectedItem();
					tabPane.getTabs().remove(tab);
					new TabStage(tab);
				}
				DetachableTabPane.DRAG_SOURCE = null;
				event.consume();
			}
		});

	}
	
	/**
	 * Set factory to generate the Scene. Default SceneFactory is provided and it will
	 * generate a scene with TabPane as root node. Call this method if you need to have
	 * a custom scene
	 * @param sceneFactory 
	 */
	public void setSceneFactory(Callback<DetachableTabPane, Scene> sceneFactory) {
		this.sceneFactory = sceneFactory;
	}
	
	/**
	 * By default, the stage owner is the stage that own the first TabPane. For example,
	 * detaching a Tab will open a new Stage. The new stage owner is the stage of the TabPane.
	 * Detaching a tab from the new stage will open another stage. Their owner are the same
	 * which is the stage of the first TabPane.
	 * @param stageOwnerFactory 
	 */
	public void setStageOwnerFactory(Callback<Stage, Window> stageOwnerFactory) {
		this.stageOwnerFactory = stageOwnerFactory;
	}
	
	private static final int STAGE_WIDTH = 400;
	private Callback<DetachableTabPane, Scene> sceneFactory = new Callback<DetachableTabPane, Scene>() {
		
		@Override
		public Scene call(DetachableTabPane p) {
			return new Scene(p, STAGE_WIDTH, STAGE_WIDTH);
		}
	};
	
	private Callback<Stage, Window> stageOwnerFactory = new Callback<Stage, Window>() {

		@Override
		public Window call(Stage p) {
			return DetachableTabPane.this.getScene().getWindow();
		}
	};

	private class TabStage extends Stage {
		private DetachableTabPane tabPane = new DetachableTabPane();

		public TabStage(final Tab tab) {
			super();
			tabPane.setSceneFactory(DetachableTabPane.this.sceneFactory);
			tabPane.setStageOwnerFactory(DetachableTabPane.this.stageOwnerFactory);
			tabPane.setScope(getScope());
                        tabPane.setStyle("-fx-opacity:0.2"); //TEST   
			initOwner(stageOwnerFactory.call(this));
			Scene scene = sceneFactory.call(tabPane);
			scene.setFill(null); //TEST
			scene.getStylesheets().addAll(DetachableTabPane.this.getScene().getStylesheets());
			setScene(scene);
                       // scene.setFill(Color.TRANSPARENT); //TEST

			if (TiwulFXUtil.isMac()) {
				com.sun.glass.ui.Robot robot =
						com.sun.glass.ui.Application.GetApplication().createRobot();

				setX(robot.getMouseX() - (STAGE_WIDTH / 2));
				setY(robot.getMouseY());
			} else {
				Point p = MouseInfo.getPointerInfo().getLocation();
				setX(p.x - (STAGE_WIDTH / 2));
				setY(p.y);
			}
			show();
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					tabPane.getTabs().add(tab);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							tabPane.getSelectionModel().select(tab);
							if (tab.getContent() instanceof Parent) {
								((Parent) tab.getContent()).requestLayout();
							}
						}
					});
				}
			});
			tabPane.getTabs().addListener(new InvalidationListener() {
				@Override
				public void invalidated(Observable o) {
					if (tabPane.getTabs().isEmpty()) {
						TabStage.this.close();
					}
				}
			});
		}
	}
}
