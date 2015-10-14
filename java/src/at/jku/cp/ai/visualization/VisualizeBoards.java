package at.jku.cp.ai.visualization;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.Action;
import prefuse.action.ActionList;
import prefuse.action.ItemAction;
import prefuse.action.RepaintAction;
import prefuse.action.animate.ColorAnimator;
import prefuse.action.animate.LocationAnimator;
import prefuse.action.animate.QualityControlAnimator;
import prefuse.action.animate.VisibilityAnimator;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.filter.FisheyeTreeFilter;
import prefuse.action.layout.CollapsedSubtreeLayout;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.SlowInSlowOutPacer;
import prefuse.controls.ControlAdapter;
import prefuse.controls.FocusControl;
import prefuse.controls.PanControl;
import prefuse.controls.WheelZoomControl;
import prefuse.controls.ZoomControl;
import prefuse.controls.ZoomToFitControl;
import prefuse.data.Edge;
import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.data.tuple.TupleSet;
import prefuse.render.AbstractShapeRenderer;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.EdgeRenderer;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.visual.VisualItem;
import prefuse.visual.expression.InGroupPredicate;
import prefuse.visual.sort.TreeDepthItemSorter;
import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.nodes.IBoardNode;

public class VisualizeBoards extends Display
{
	private static final long serialVersionUID = 8618067074945202358L;

	private static final String keyTree = "tree";
	private static final String keyTreeNodes = "tree.nodes";
	private static final String keyTreeEdges = "tree.edges";

	private final LabelRenderer m_nodeRenderer;
	private final EdgeRenderer m_edgeRenderer;

	private int m_orientation = Constants.ORIENT_LEFT_RIGHT;

	public VisualizeBoards(final Tree tree)
	{
		super(new Visualization());

		m_vis.add(keyTree, tree);
		
		m_nodeRenderer = new BoardRenderer("lastMove", "board");
		m_nodeRenderer.setRenderType(AbstractShapeRenderer.RENDER_TYPE_FILL);
		m_nodeRenderer.setHorizontalAlignment(Constants.RIGHT);
		m_nodeRenderer.setImagePosition(Constants.RIGHT);
		m_nodeRenderer.setRoundedCorner(8, 8);
		m_edgeRenderer = new EdgeRenderer(Constants.EDGE_TYPE_CURVE);

		DefaultRendererFactory rf = new DefaultRendererFactory();
		rf.add(new InGroupPredicate(keyTreeNodes), m_nodeRenderer);
		rf.add(new InGroupPredicate(keyTreeEdges), m_edgeRenderer);
		m_vis.setRendererFactory(rf);

		int[] palette = new int[]
		{
			ColorLib.rgb(0, 0, 255),
			ColorLib.rgb(255, 0, 0)
		};

		ItemAction nodeColor = new NodeColorAction(keyTreeNodes);
		ColorAction edgeColor = new DataColorAction(keyTreeEdges, "lastMovePlayedBy", Constants.NOMINAL, VisualItem.STROKECOLOR, palette);
		ColorAction textColor = new DataColorAction(keyTreeNodes, "lastMovePlayedBy", Constants.NOMINAL, VisualItem.TEXTCOLOR, palette);

		// create the tree layout action
		NodeLinkTreeLayout treeLayout = new NodeLinkTreeLayout(keyTree, m_orientation, 50, 0, 8);
		treeLayout.setLayoutAnchor(new Point2D.Double(25, 300));
		m_vis.putAction("treeLayout", treeLayout);

		CollapsedSubtreeLayout subLayout = new CollapsedSubtreeLayout(keyTree, m_orientation);
		m_vis.putAction("subLayout", subLayout);

		AutoPanAction autoPan = new AutoPanAction();

		// create the filtering and layout
		ActionList filter = new ActionList();
		filter.add(new FisheyeTreeFilter(keyTree, 2));
		filter.add(new FontAction(keyTreeNodes, FontLib.getFont("DejaVu Sans Mono", 16)));
		filter.add(treeLayout);
		filter.add(subLayout);
//		filter.add(new Action() {
//			@Override
//			public void run(double frac)
//			{
//				System.out.println("filter");
//			}
//		});
		m_vis.putAction("filter", filter);

		ActionList color = new ActionList();
		color.add(textColor);
		color.add(nodeColor);
		color.add(edgeColor);
//		color.add(new Action() {
//			@Override
//			public void run(double frac)
//			{
//				System.out.println("color");
//			}
//		});
		m_vis.putAction("color", color);
		m_vis.alwaysRunAfter("filter", "color");
		
		// animated transition
		ActionList animate = new ActionList(200);
		animate.setPacingFunction(new SlowInSlowOutPacer());
		animate.add(autoPan);
		animate.add(new QualityControlAnimator());
		animate.add(new VisibilityAnimator(keyTree));
		animate.add(new LocationAnimator(keyTreeNodes));
		animate.add(new ColorAnimator(keyTreeNodes));
		animate.add(new ColorAnimator(keyTreeEdges));
		animate.add(new RepaintAction());
//		animate.add(new Action() {
//			@Override
//			public void run(double frac)
//			{
//				System.out.println("animate");
//			}
//		});
		m_vis.putAction("animate", animate);
		m_vis.alwaysRunAfter("filter", "animate");

		// create animator for orientation changes
		ActionList orient = new ActionList(500);
		orient.setPacingFunction(new SlowInSlowOutPacer());
		orient.add(autoPan);
		orient.add(new QualityControlAnimator());
		orient.add(new LocationAnimator(keyTreeNodes));
		orient.add(new RepaintAction());
//		orient.add(new Action() {
//			@Override
//			public void run(double frac)
//			{
//				System.out.println("orient");
//			}
//		});
		m_vis.putAction("orient", orient);

		// initialize the display
		setSize(700, 600);
		setItemSorter(new TreeDepthItemSorter());
		addControlListener(new ZoomToFitControl());
		addControlListener(new ZoomControl());
		addControlListener(new WheelZoomControl());
		addControlListener(new PanControl());
		addControlListener(new FocusControl(1, "filter"));

		// RAU:
		// this is where the graph is built, move by move
		addControlListener(new ControlAdapter()
		{
			@Override
			public void itemClicked(VisualItem item, MouseEvent e)
			{
				Node parent = tree.getNode(item.getRow());

				if (parent.getChildCount() == 0)
				{
					IBoard parentBoard = (IBoard) parent.get("board");
					IBoardNode parentBoardNode = new IBoardNode(parentBoard);
					for (at.jku.cp.ai.search.Node childBoardNode : parentBoardNode.adjacent())
					{
						IBoard childBoard = (IBoard) childBoardNode.getState();
						Node child = tree.addChild(parent);
						Edge edge = tree.getEdge(parent, child);
						setEdgeFromBoard(edge, parentBoard, childBoard);
						setNodeFromBoard(child, parentBoard, childBoard);
					}
				}
				m_vis.run("color");
			}
		});

		registerKeyboardAction(new OrientAction(Constants.ORIENT_LEFT_RIGHT), "left-to-right", KeyStroke.getKeyStroke("ctrl 1"), WHEN_FOCUSED);
		registerKeyboardAction(new OrientAction(Constants.ORIENT_TOP_BOTTOM), "top-to-bottom", KeyStroke.getKeyStroke("ctrl 2"), WHEN_FOCUSED);
		registerKeyboardAction(new OrientAction(Constants.ORIENT_RIGHT_LEFT), "right-to-left", KeyStroke.getKeyStroke("ctrl 3"), WHEN_FOCUSED);
		registerKeyboardAction(new OrientAction(Constants.ORIENT_BOTTOM_TOP), "bottom-to-top", KeyStroke.getKeyStroke("ctrl 4"), WHEN_FOCUSED);

		// filter graph and perform layout
		setOrientation(m_orientation);
		m_vis.run("filter");
	}

	// ------------------------------------------------------------------------

	public void setOrientation(int orientation)
	{
		NodeLinkTreeLayout rtl = (NodeLinkTreeLayout) m_vis.getAction("treeLayout");
		CollapsedSubtreeLayout stl = (CollapsedSubtreeLayout) m_vis.getAction("subLayout");
		switch (orientation)
		{
		case Constants.ORIENT_LEFT_RIGHT:
			m_nodeRenderer.setHorizontalAlignment(Constants.LEFT);
			m_edgeRenderer.setHorizontalAlignment1(Constants.RIGHT);
			m_edgeRenderer.setHorizontalAlignment2(Constants.LEFT);
			m_edgeRenderer.setVerticalAlignment1(Constants.CENTER);
			m_edgeRenderer.setVerticalAlignment2(Constants.CENTER);
			break;
		case Constants.ORIENT_RIGHT_LEFT:
			m_nodeRenderer.setHorizontalAlignment(Constants.RIGHT);
			m_edgeRenderer.setHorizontalAlignment1(Constants.LEFT);
			m_edgeRenderer.setHorizontalAlignment2(Constants.RIGHT);
			m_edgeRenderer.setVerticalAlignment1(Constants.CENTER);
			m_edgeRenderer.setVerticalAlignment2(Constants.CENTER);
			break;
		case Constants.ORIENT_TOP_BOTTOM:
			m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
			m_edgeRenderer.setHorizontalAlignment1(Constants.CENTER);
			m_edgeRenderer.setHorizontalAlignment2(Constants.CENTER);
			m_edgeRenderer.setVerticalAlignment1(Constants.BOTTOM);
			m_edgeRenderer.setVerticalAlignment2(Constants.TOP);
			break;
		case Constants.ORIENT_BOTTOM_TOP:
			m_nodeRenderer.setHorizontalAlignment(Constants.CENTER);
			m_edgeRenderer.setHorizontalAlignment1(Constants.CENTER);
			m_edgeRenderer.setHorizontalAlignment2(Constants.CENTER);
			m_edgeRenderer.setVerticalAlignment1(Constants.TOP);
			m_edgeRenderer.setVerticalAlignment2(Constants.BOTTOM);
			break;
		default:
			throw new IllegalArgumentException("Unrecognized orientation value: " + orientation);
		}
		m_orientation = orientation;
		rtl.setOrientation(orientation);
		stl.setOrientation(orientation);
	}

	public int getOrientation()
	{
		return m_orientation;
	}

	private static void setNodeFromBoard(Node node, IBoard parentBoard, IBoard childBoard)
	{
		node.set("board", childBoard);
		node.set("currentUnicorn", childBoard.getCurrentUnicorn().id);
		node.set("lastMovePlayedBy", parentBoard.getCurrentUnicorn().id);
	}

	private static void setEdgeFromBoard(Edge edge, IBoard parentBoard, IBoard childBoard)
	{
		//System.out.println("lmpb" + parentBoard.getCurrentUnicorn().id);
		edge.set("lastMovePlayedBy", parentBoard.getCurrentUnicorn().id);
	}
	
	private static VisualizeBoards makeTree(String filename)
	{
		IBoard board = Board.fromLevelFile(filename);
		final Tree tree = new Tree();

		tree.addColumn("board", IBoard.class);
		tree.addColumn("currentUnicorn", Integer.class);
		tree.addColumn("lastMove", String.class);
		tree.addColumn("lastMovePlayedBy", Integer.class);

		Node root = tree.addRoot();
		setNodeFromBoard(root, board, board);

		VisualizeBoards treeview = new VisualizeBoards(tree);
		treeview.setBackground(Color.WHITE);
		treeview.setForeground(Color.BLACK);

		return treeview;
	}

	public static void main(String argv[])
	{
		JFrame frame = new JFrame("Visu");
		Container content = frame.getContentPane();
		Container treeContainer = new JPanel();

		content.setLayout(new BorderLayout());
		content.add(treeContainer, BorderLayout.CENTER);

		treeContainer.add(makeTree(at.jku.cp.ai.utils.Constants.ASSET_PATH + "/default.lvl"));

		JMenuBar menubar = new JMenuBar();
		JMenu levelMenu = new JMenu("Level");
		levelMenu.setMnemonic(KeyEvent.VK_L);

		final JFileChooser fileChooser = new JFileChooser(new File(at.jku.cp.ai.utils.Constants.ASSET_PATH));

		final JMenuItem openLevel = new JMenuItem("Open Levelfile");
		openLevel.setMnemonic(KeyEvent.VK_O);
		openLevel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getSource() == openLevel)
				{
					int returnVal = fileChooser.showOpenDialog(frame);

					if (returnVal == JFileChooser.APPROVE_OPTION)
					{
						File file = fileChooser.getSelectedFile();

						try
						{
							VisualizeBoards vb = makeTree(file.getAbsolutePath());

							treeContainer.removeAll();
							treeContainer.add(vb);
						}
						catch (Exception ex)
						{
							System.out.println(ex);
						}
					}
				}

			}
		});
		levelMenu.add(openLevel);

		JMenu searcherMenu = new JMenu("Searcher");
		menubar.add(levelMenu);
		menubar.add(searcherMenu);

		content.add(menubar, BorderLayout.PAGE_START);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	// ------------------------------------------------------------------------

	public class OrientAction extends AbstractAction
	{
		private static final long serialVersionUID = 1375306717454500825L;
		private int orientation;

		public OrientAction(int orientation)
		{
			this.orientation = orientation;
		}

		public void actionPerformed(ActionEvent evt)
		{
			setOrientation(orientation);
			getVisualization().cancel("orient");
			getVisualization().run("treeLayout");
			getVisualization().run("orient");
		}
	}

	public class AutoPanAction extends Action
	{
		private Point2D m_start = new Point2D.Double();
		private Point2D m_end = new Point2D.Double();
		private Point2D m_cur = new Point2D.Double();
		private int m_bias = 150;

		public void run(double frac)
		{
			TupleSet ts = m_vis.getFocusGroup(Visualization.FOCUS_ITEMS);
			if (ts.getTupleCount() == 0)
				return;

			if (frac == 0.0)
			{
				int xbias = 0, ybias = 0;
				switch (m_orientation)
				{
				case Constants.ORIENT_LEFT_RIGHT:
					xbias = m_bias;
					break;
				case Constants.ORIENT_RIGHT_LEFT:
					xbias = -m_bias;
					break;
				case Constants.ORIENT_TOP_BOTTOM:
					ybias = m_bias;
					break;
				case Constants.ORIENT_BOTTOM_TOP:
					ybias = -m_bias;
					break;
				}

				VisualItem vi = (VisualItem) ts.tuples().next();
				m_cur.setLocation(getWidth() / 2, getHeight() / 2);
				getAbsoluteCoordinate(m_cur, m_start);
				m_end.setLocation(vi.getX() + xbias, vi.getY() + ybias);
			} else
			{
				m_cur.setLocation(m_start.getX() + frac * (m_end.getX() - m_start.getX()), m_start.getY() + frac * (m_end.getY() - m_start.getY()));
				panToAbs(m_cur);
			}
		}
	}

	public static class NodeColorAction extends ColorAction
	{

		public NodeColorAction(String group)
		{
			super(group, VisualItem.FILLCOLOR);
		}

		public int getColor(VisualItem item)
		{
			if (m_vis.isInGroup(item, Visualization.SEARCH_ITEMS))
				return ColorLib.rgb(255, 190, 190);
			else if (m_vis.isInGroup(item, Visualization.FOCUS_ITEMS))
				return ColorLib.rgb(198, 229, 229);
			else if (item.getDOI() > -1)
				return ColorLib.rgb(164, 193, 193);
			else
				return ColorLib.rgba(255, 255, 255, 0);
		}

	} // end of inner class TreeMapColorAction

} // end of class TreeMap