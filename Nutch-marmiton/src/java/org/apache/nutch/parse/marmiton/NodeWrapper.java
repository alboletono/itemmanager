package org.apache.nutch.parse.marmiton;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl;

public class NodeWrapper {

	public static NodeWrapper NULL_NODE = new NodeWrapper();

	private Node node;

	private NodeWrapper() {
		super();
		this.node = new NodeImpl();
	}

	public NodeWrapper(Node node) {
		this();
		this.node = node;
	}

	public NodeWrapper getChildWithTag(String tag, String attributeName) {
		NodeWrapper result = NULL_NODE;

		if (this.node.hasChildNodes()) {
			for (int i = 0; i < this.node.getChildNodes().getLength(); i++) {
				Node node = this.node.getChildNodes().item(i);
				boolean matches = node.getNodeName().equals(tag);
				if (attributeName != null && node.hasAttributes()) {
					matches = matches
							&& node.getAttributes().getNamedItem(attributeName) != null;
					
				}
				if (matches) {
					result = new NodeWrapper(node);
					break;
				}
			}
		}

		return result;
	}

	public NodeWrapper getChildWithTag(String tag) {
		return this.getChildWithTag(tag, null);
	}

	/**
	 * If it matches the node, the node itself is returned. Otherwise the fake
	 * null node.
	 * 
	 * @param node
	 * @param tag
	 * @param attributeName
	 * @param attributeValue
	 * @return If it matches the node, the node itself is returned. Otherwise
	 *         the fake null node.
	 */
	public NodeWrapper matchesTagWithAttribute(String tag,
			String attributeName, String attributeValue) {
		boolean matches = false;

		if (tag.equals(this.node.getNodeName())) {
			NamedNodeMap attributes = this.node.getAttributes();
			if (attributes != null) {
				Node attribute = attributes.getNamedItem(attributeName);
				matches = attribute != null;

				// If an attribute value is provided, we use it for the
				// comparison.
				if (attributeValue != null) {
					matches = matches
							&& attributeValue.equals(attribute.getNodeValue());
				}
			}
		}
		if (matches) {
			return new NodeWrapper(this.node);
		} else {
			return NULL_NODE;
		}

	}

	/**
	 * If it matches the node, the node itself is returned. Otherwise the fake
	 * null node. No check is done on the attribute value.
	 * 
	 * @param node
	 * @param tag
	 * @param attributeName
	 * @return If it matches the node, the node itself is returned. Otherwise
	 *         the fake null node.
	 */
	public NodeWrapper matchesTagWithAttribute(String tag, String attributeName) {

		return this.matchesTagWithAttribute(tag, attributeName, null);
	}

	public Node getNode() {
		return node;
	}

}
