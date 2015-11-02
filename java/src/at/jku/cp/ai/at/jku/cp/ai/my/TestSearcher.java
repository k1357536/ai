package at.jku.cp.ai.at.jku.cp.ai.my;


import at.jku.cp.ai.rau.Board;
import at.jku.cp.ai.rau.IBoard;
import at.jku.cp.ai.rau.functions.IBoardPredicate;
import at.jku.cp.ai.rau.nodes.AlwaysMoveNode;
import at.jku.cp.ai.rau.objects.Fountain;
import at.jku.cp.ai.search.Node;
import at.jku.cp.ai.search.Search;
import at.jku.cp.ai.search.algorithms.*;
import at.jku.cp.ai.utils.Constants;
import at.jku.cp.ai.utils.PathUtils;
import at.jku.cp.ai.utils.RenderUtils;

import java.util.function.Predicate;

/**
 * Just a test class, so i could debug the search algorithm
 * without unit test timeout
 */
public class TestSearcher {
    public static void main(String argv[]) throws Exception {
        int lvlNr = 0;
        String pathToLevel = String.format(Constants.ASSET_PATH + "/assignment1/L%d", lvlNr);

        IBoard board = Board.fromLevelFile(pathToLevel + "/level");
        PathUtils.fromFile(pathToLevel + "/bfs.path");
        Class<?> nodeClazz = AlwaysMoveNode.class;
        Search searcher =  new DLDFS(16);

        final Fountain end = board.getFountains().get(0);
        Predicate<Node> endReached = new IBoardPredicate(b -> b.isRunning() && b.getCurrentUnicorn().pos.equals(end.pos));

        Node startNode = (Node) nodeClazz.getDeclaredConstructor(IBoard.class).newInstance(board);
        Node endNode = searcher.search(startNode, endReached);

        if (null == endNode) {
            System.out.println("goal not found!");
            return;
        }
        System.out.println(RenderUtils.visualizePath(board, PathUtils.getPath(endNode)));
    }
}
