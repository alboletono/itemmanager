package org.apache.nutch.parse.amisfinder;

import org.apache.nutch.util.NodeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl;

/**
 * Some utilities to get html tag/attributes using TagSoup.
 * 
 * @author avigier
 * 
 */
public class NodeWrapper {

	private Node node;

	/** A null node when no matching resulting node */
	public static NodeWrapper NULL_NODE = new NodeWrapper();

	private static final Logger LOG = LoggerFactory
			.getLogger(NodeWrapper.class);

	private NodeWrapper() {
		super();
		this.node = new NodeImpl();
	}

	/**
	 * 
	 * @param node
	 *            the node on which getting html information
	 */
	public NodeWrapper(Node node) {
		this();
		this.node = node;
	}

	/**
	 * 
	 * @param tag
	 *            the tag of the child to get
	 * @param attributeName
	 *            the attribute name of the child to get (if specified)
	 * @param attributeValue
	 *            the attribute value of the child to get (if specified)
	 * @return the nodewrapper that corresponds to the query
	 */
	public NodeWrapper getChildWithTag(String tag, String attributeName,
			String attributeValue) {
		NodeWrapper result = NULL_NODE;

		if (this.node.hasChildNodes()) {
			for (int i = 0; i < this.node.getChildNodes().getLength(); i++) {
				Node current = this.node.getChildNodes().item(i);
				boolean matches = current.getNodeName().equals(tag);
				// An attribute has been specified
				if (attributeName != null && current.hasAttributes()) {
					matches = matches
							&& current.getAttributes().getNamedItem(
									attributeName) != null;
					// An attribute value has been specified
					if (attributeValue != null && matches) {
						matches = attributeValue.equals(current.getAttributes()
								.getNamedItem(attributeName).getNodeValue());
					}

				}
				if (matches) {
					result = new NodeWrapper(current);
					break;
				}
			}
		}

		return result;
	}

	/**
	 * 
	 * @param walker
	 * @param tag
	 *            the tag name to find
	 * @return the found node if any
	 */
	public NodeWrapper findNextElement(NodeWalker walker, String tag) {
		NodeWrapper result = null;
		boolean found = false;
		Node current = null;
		while (walker.hasNext() && !found) {
			current = walker.nextNode();
			found = current.getNodeType() == Node.ELEMENT_NODE;
		}

		// Checking if the found element matches the expected tag
		if (found && current != null && tag.equals(current.getNodeName())) {
			result = new NodeWrapper(current);
		} else {
			result = NULL_NODE;
		}

		return result;
	}

	/**
	 * 
	 * @param tag
	 *            the tag of the child to get.
	 * @return the node wrapper that corresponds to the query.
	 */
	public NodeWrapper getChildWithTag(String tag) {
		return this.getChildWithTag(tag, null, null);
	}

	/**
	 * If it matches the node, the node itself is returned. Otherwise the fake
	 * null node.
	 * 
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

				// If an attribute value is provided, we use it for the
				// comparison.
				if (attributeValue != null && attribute != null) {
					matches = attributeValue.equals(attribute.getNodeValue());
				}
			}
		}
		NodeWrapper result = null;
		if (matches) {
			result = new NodeWrapper(this.node);
		} else {
			result = NULL_NODE;
		}
		return result;

	}

	/**
	 * 
	 * @param tag
	 *            the tag to find
	 * @param content
	 *            the content to match
	 * @return the node itself otherwise the null node.
	 */
	public NodeWrapper matchesTagWithContent(String tag, String content) {

		boolean matches = false;
		if (this.node.getTextContent() != null) {
			try {
				String utf8Content = new String(this.node.getTextContent()
						.getBytes(), "UTF-8");

				matches = tag.equals(this.node.getNodeName())
						&& content.equals(utf8Content.trim());
			} catch (Exception e) {
				LOG.error("Cannot convert string to utf-8: "
						+ this.node.getTextContent());
			}
		}

		NodeWrapper result = null;
		if (matches) {
			result = this;
		} else {
			result = NULL_NODE;
		}
		return result;
	}

	/**
	 * If it matches the node, the node itself is returned. Otherwise the fake
	 * null node. No check is done on the attribute value.
	 * 
	 * @param tag
	 * @param attributeName
	 * @return If it matches the node, the node itself is returned. Otherwise
	 *         the fake null node.
	 */
	public NodeWrapper matchesTagWithAttribute(String tag, String attributeName) {

		return this.matchesTagWithAttribute(tag, attributeName, null);
	}

	/**
	 * 
	 * @return the current node.
	 */
	public Node getNode() {
		return this.node;
	}

	/**
	 * 
	 * @param name
	 *            the name of the attribute to get
	 * @return the value of the attribute if it exists, null otherwise.
	 */
	public String getAttributeValue(String name) {
		String result = null;

		if (this.node.getAttributes() != null
				&& this.node.getAttributes().getNamedItem(name) != null) {
			result = this.node.getAttributes().getNamedItem(name)
					.getNodeValue().trim();
		}

		return result;
	}

	/**
	 * 
	 * @param string
	 *            the string from which stripping html tags.
	 * @return
	 */
	private String stripHtmlTags(String string) {
		return string.replaceAll("\\<.*?\\>", "");

	}

	/**
	 * Rewrites the Node.getTextContent to allow a strip of the tags and a trim.
	 * @param string
	 * @return the text content cleaned.
	 */
	public String getTextContent() {
		String result = this.node.getTextContent();
		if (result != null)
			result = this.stripHtmlTags(result.trim());
		return result;

	}

}
