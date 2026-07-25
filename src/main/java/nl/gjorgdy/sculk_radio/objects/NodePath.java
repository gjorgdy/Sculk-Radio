package nl.gjorgdy.sculk_radio.objects;

import nl.gjorgdy.sculk_radio.objects.nodes.abstracts.Node;

import java.util.Arrays;
import java.util.function.BiConsumer;

public class NodePath {

	private final Node[] path;

	private NodePath(Node[] path, Node next) {
		this.path = Arrays.copyOf(path, path.length + 1);
		this.path[path.length] = next;
	}

	private NodePath(Node start) {
		this.path = new Node[]{start};
	}

	public static NodePath of(Node start) {
		return new NodePath(start);
	}

	public NodePath append(Node next) {
		return new NodePath(path, next);
	}

	public void forEach(BiConsumer<Node, Node> biConsumer) {
		for (int i = 0; i < path.length - 1; i++) {
			biConsumer.accept(path[i], path[i + 1]);
		}
	}

}
