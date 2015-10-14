package at.jku.cp.ai.visualization;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.List;

import prefuse.render.LabelRenderer;
import prefuse.visual.VisualItem;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.objects.GameObject;
import at.jku.cp.ai.utils.RenderUtils;

public class BoardRenderer extends LabelRenderer {

	public BoardRenderer(String textField, String imageField) {
		setTextField(textField);
		setImageField(imageField);
	}

	@Override
	protected Image getImage(VisualItem item) {
		IBoard board = (IBoard) item.get("board");
		return render(board);
	}

	private Image render(IBoard board) {
		int w = 8;

		BufferedImage image = new BufferedImage(
				board.getWidth() * w,
				board.getHeight() * w,
				BufferedImage.TYPE_INT_RGB
		);

		Graphics2D gfx = image.createGraphics();
		for (List<? extends GameObject> objs : board.getAllObjects()) {
			for (GameObject g : objs) {
				RenderUtils.draw(gfx, g, w);
			}
		}
		return image;
	}
}
