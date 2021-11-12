package com.hdsx.geohome.geocoding.utile;


import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XPathParser {
	  private Document document;
	  private boolean validation;
	  private EntityResolver entityResolver;
	  private XPath xpath;
	  private ErrorHandler errorHandler;
	  
	  public XPathParser(InputStream inputStream) {
	    commonConstructor(false, null, null);
	    this.document = createDocument(new InputSource(inputStream));
	  }

	  public XPathParser(InputStream inputStream, boolean validation) {
	    commonConstructor(validation, null, null);
	    this.document = createDocument(new InputSource(inputStream));
	  }

	  public XPathParser(InputStream inputStream, boolean validation, ErrorHandler errorHandler) {
	    commonConstructor(validation, null,errorHandler);
	    this.document = createDocument(new InputSource(inputStream));
	  }
	  public String evalString(String expression) {
	    return evalString(document, expression);
	  }

	  public String evalString(Object root, String expression) {
	    String result = (String) evaluate(expression, root, XPathConstants.STRING);
	    return result;
	  }

	  public Boolean evalBoolean(String expression) {
	    return evalBoolean(document, expression);
	  }

	  public Boolean evalBoolean(Object root, String expression) {
	    return (Boolean) evaluate(expression, root, XPathConstants.BOOLEAN);
	  }

	  public Short evalShort(String expression) {
	    return evalShort(document, expression);
	  }

	  public Short evalShort(Object root, String expression) {
	    return Short.valueOf(evalString(root, expression));
	  }

	  public Integer evalInteger(String expression) {
	    return evalInteger(document, expression);
	  }

	  public Integer evalInteger(Object root, String expression) {
	    return Integer.valueOf(evalString(root, expression));
	  }

	  public Long evalLong(String expression) {
	    return evalLong(document, expression);
	  }

	  public Long evalLong(Object root, String expression) {
	    return Long.valueOf(evalString(root, expression));
	  }

	  public Float evalFloat(String expression) {
	    return evalFloat(document, expression);
	  }

	  public Float evalFloat(Object root, String expression) {
	    return Float.valueOf(evalString(root, expression));
	  }

	  public Double evalDouble(String expression) {
	    return evalDouble(document, expression);
	  }

	  public Double evalDouble(Object root, String expression) {
	    return (Double) evaluate(expression, root, XPathConstants.NUMBER);
	  }
	  
	  public List<XNode> evalNodes(String expression) {
	    return evalNodes(document, expression);
	  }

	  public List<XNode> evalNodes(Object root, String expression) {
	    List<XNode> xnodes = new ArrayList<XNode>();
	    NodeList nodes = (NodeList) evaluate(expression, root, XPathConstants.NODESET);
	    for (int i = 0; i < nodes.getLength(); i++) {
	      xnodes.add(new XNode(this, nodes.item(i)));
	    }
	    return xnodes;
	  }

	  public XNode evalNode(String expression) {
	    return evalNode(document, expression);
	  }

	  public XNode evalNode(Object root, String expression) {
	    Node node = (Node) evaluate(expression, root, XPathConstants.NODE);
	    if (node == null) {
	      return null;
	    }
	    return new XNode(this, node);
	  }

	  private Object evaluate(String expression, Object root, QName returnType) {
	    try {
	      return xpath.evaluate(expression, root, returnType);
	    } catch (Exception e) {
			System.out.println("Error evaluating XPath.  Cause: " );
	    }
		  return null;
	  }

	  private Document createDocument(InputSource inputSource) {
	    // important: this must only be called AFTER common constructor
	    try {
	      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	      factory.setValidating(validation);
	      factory.setNamespaceAware(false);
	      factory.setIgnoringComments(true);
	      factory.setIgnoringElementContentWhitespace(false);
	      factory.setCoalescing(false);
	      factory.setExpandEntityReferences(true);

	      DocumentBuilder builder = factory.newDocumentBuilder();
	      builder.setEntityResolver(entityResolver);
	      builder.setErrorHandler(errorHandler);
	      return builder.parse(inputSource);
	    } catch (Exception e) {
			System.out.println("Error creating document instance.  Cause: " );
	    }
		  return null;
	  }

	  private void commonConstructor(boolean validation,  EntityResolver entityResolver,ErrorHandler errorHandler) {
	    this.validation = validation;
	    this.entityResolver = entityResolver;
	    this.errorHandler=errorHandler;
	    XPathFactory factory = XPathFactory.newInstance();
	    this.xpath = factory.newXPath();
	  }
}

