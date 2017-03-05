package monolith52.adjustimage.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class PercentageCell<S> extends TableCell<S, Double> {
	private Pane root = new Pane();
	private Rectangle fill = new Rectangle();
	private Label label = new Label();
	private static Color barColor = new Color(202.0d/255, 225.0d/255, 223.0d/255, 1.0d);
	
	public PercentageCell() {
		root.prefWidthProperty().bind(widthProperty());
		root.prefHeightProperty().bind(heightProperty());
		label.prefWidthProperty().bind(root.widthProperty());
		label.prefHeightProperty().bind(root.heightProperty());
		label.setAlignment(Pos.BASELINE_CENTER);
		fill.setFill(barColor);
		fill.setWidth(30.0d);
		fill.heightProperty().bind(Bindings.createDoubleBinding(() -> {
			return root.getHeight() - 1;
		}, root.heightProperty()));
		root.getChildren().addAll(fill, label);
		
		setPadding(new Insets(0, 0, 0, 0));
		setBorder(null);
	}
	
	@Override
	protected void updateItem(Double item, boolean empty) {
		super.updateItem(item, empty);
		
		double rate = (empty ? 0.0d : item);
		fill.setWidth(root.getWidth() * Math.max(0.0d, Math.min(1.0d, rate)));
		label.setText((item != null) ? format(item) : "");
		setGraphic(root);
	}
	
	protected String format(Double item) {
		return String.format("%.2f%%", item * 100);
	}
}
