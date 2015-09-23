package com.simbrella.InfoCell;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

/**
 * Created with IntelliJ IDEA. User: iiskenderov Date: 9/17/13 Time: 1:59 PM To
 * change this template use File | Settings | File Templates.
 */

@SuppressWarnings("unused")
public class Menu {
	public Menu Parent;

	public String Title;
	public int Type = 1;
	public String CMD;
	public String Keyword;
	public int InputCount;
	public int InputType;

	public List<Menu> Submenus = new ArrayList<Menu>();

	public List<String> Labels = new ArrayList<String>();
	public List<CharSequence> adapterList = new ArrayList<CharSequence>();

	public List<DataListItem> List1 = new ArrayList<DataListItem>();
	public List<DataListItem> List2 = new ArrayList<DataListItem>();

	public String List1Label;
	public String List2Label;

	public String ID;

	public Menu() {
		Type = 1; // submenu
		Submenus = new ArrayList<Menu>();
		Labels = new ArrayList<String>();
		List1 = new ArrayList<DataListItem>();
		List2 = new ArrayList<DataListItem>();
	}

	public Menu(Node xElement, Menu parent, List<Menu> AllMenus) {
		this();
		try {
			AllMenus.add(this);

			Parent = parent;

			ID = UUID.randomUUID().toString();

			if (xElement.getAttributes().getNamedItem("title") == null) {
				// root menu

				for (int i = 0; i < xElement.getChildNodes().getLength(); i++) {
					if (xElement.getChildNodes().item(i).getNodeType() != Node.TEXT_NODE) {
						Menu menu = new Menu(xElement.getChildNodes().item(i),
								this, AllMenus);
						Submenus.add(menu);
					}
				}
			} else {
				Title = xElement.getAttributes().getNamedItem("title")
						.getNodeValue().toString();
				Type = Integer.parseInt(xElement.getAttributes()
						.getNamedItem("type").getNodeValue().toString());

				Keyword = (xElement.getAttributes().getNamedItem("keyword") != null) ? xElement
						.getAttributes().getNamedItem("keyword").getNodeValue()
						.toString()
						: "";
				CMD = (xElement.getAttributes().getNamedItem("cmd") != null) ? xElement
						.getAttributes().getNamedItem("cmd").getNodeValue()
						.toString()
						: "";

				if (Type == 1) {
					// submenu
					for (int i = 0; i < xElement.getChildNodes().getLength(); i++) {
						if (xElement.getChildNodes().item(i).getNodeType() != Node.TEXT_NODE) {
							Menu menu = new Menu(xElement.getChildNodes().item(
									i), this, AllMenus);
							Submenus.add(menu);
						}
					}
				} else if (Type == 2) {
					// direct web call
					// nothing to load, already got everything
				} else if (Type == 3) {
					// 3 - web request with text input from user (inputCount -
					// count of text inputs) / inputType (0-text, 1-phone
					// number) , input1Label,input2Label.. - labels for text
					// inputs

					InputCount = Integer.parseInt(xElement.getAttributes()
							.getNamedItem("inputCount").getNodeValue()
							.toString());
					InputType = (xElement.getAttributes().getNamedItem(
							"inputType") != null) ? Integer.parseInt(xElement
							.getAttributes().getNamedItem("inputType")
							.getNodeValue().toString()) : 0;

					for (int i = 1; i <= InputCount; i++) {
						if (xElement.getAttributes().getNamedItem(
								String.format("input%dLabel",
										Integer.valueOf(i))) != null) {
							Labels.add(xElement
									.getAttributes()
									.getNamedItem(
											String.format("input%dLabel",
													Integer.valueOf(i)))
									.getNodeValue().toString());
						}
					}

					if (InputCount > 0 && Labels.size() == 0) {
						// if no labels provided add title as label
						Labels.add(Title);
					}
				} else if (Type == 4) {
					// 4 - web request with selection from two lists, inputCount
					// count of additional text fields, firstLabel. secondLabel
					// - labesl for lists, input1Label,input2Label.. - labels
					// for text inputs

					InputCount = (xElement.getAttributes().getNamedItem(
							"inputCount") != null) ? Integer.parseInt(xElement
							.getAttributes().getNamedItem("inputCount")
							.getNodeValue().toString()) : 0;
					InputType = (xElement.getAttributes().getNamedItem(
							"inputType") != null) ? Integer.parseInt(xElement
							.getAttributes().getNamedItem("inputType")
							.getNodeValue().toString()) : 0;

					List1Label = (xElement.getAttributes().getNamedItem(
							"firstLabel") != null) ? xElement.getAttributes()
							.getNamedItem("firstLabel").getNodeValue()
							.toString() : "";
					List2Label = (xElement.getAttributes().getNamedItem(
							"secondLabel") != null) ? xElement.getAttributes()
							.getNamedItem("secondLabel").getNodeValue()
							.toString() : "";

					for (int i = 1; i <= InputCount; i++) {
						if (xElement.getAttributes().getNamedItem(
								String.format("input%dLabel",
										Integer.valueOf(i))) != null) {
							Labels.add(xElement
									.getAttributes()
									.getNamedItem(
											String.format("input%dLabel",
													Integer.valueOf(i)))
									.getNodeValue().toString());
						}
					}

					if (InputCount > 0 && Labels.size() == 0) {
						// if no labels provided add title as label
						Labels.add(Title);
					}

					for (int x = 0; x < xElement.getChildNodes().getLength(); x++) {
						if (xElement.getChildNodes().item(x).getNodeName()
								.equals("firstList")) {
							for (int i = 0; i < xElement.getChildNodes()
									.item(x).getChildNodes().getLength(); i++) {
								if (xElement.getChildNodes().item(x)
										.getChildNodes().item(i).getNodeType() != Node.TEXT_NODE) {
									DataListItem dt = new DataListItem();
									dt.Title = xElement.getChildNodes().item(x)
											.getChildNodes().item(i)
											.getAttributes()
											.getNamedItem("title")
											.getNodeValue().toString();
									dt.Value = xElement.getChildNodes().item(x)
											.getChildNodes().item(i)
											.getAttributes()
											.getNamedItem("value")
											.getNodeValue().toString();
									List1.add(dt);
								}
							}
						} else if (xElement.getChildNodes().item(x)
								.getNodeName().equals("secondList")) {
							for (int i = 0; i < xElement.getChildNodes()
									.item(x).getChildNodes().getLength(); i++) {
								if (xElement.getChildNodes().item(x)
										.getChildNodes().item(i).getNodeType() != Node.TEXT_NODE) {
									DataListItem dt = new DataListItem();
									dt.Title = xElement.getChildNodes().item(x)
											.getChildNodes().item(i)
											.getAttributes()
											.getNamedItem("title")
											.getNodeValue().toString();
									dt.Value = xElement.getChildNodes().item(x)
											.getChildNodes().item(i)
											.getAttributes()
											.getNamedItem("value")
											.getNodeValue().toString();
									List2.add(dt);
								}
							}
						}
					}
				} else if (Type == 5) {
					// SMS with predefined keyword
					// nothing to read
				} else if (Type == 6) {
					// SMS with input from user (inputCount - count of text
					// inputs) / inputType (0-text, 1-phone number)
					InputCount = (xElement.getAttributes().getNamedItem(
							"inputCount") != null) ? Integer.parseInt(xElement
							.getAttributes().getNamedItem("inputCount")
							.getNodeValue().toString()) : 0;
					InputType = (xElement.getAttributes().getNamedItem(
							"inputType") != null) ? Integer.parseInt(xElement
							.getAttributes().getNamedItem("inputType")
							.getNodeValue().toString()) : 0;

					for (int i = 1; i <= InputCount; i++) {
						if (xElement.getAttributes().getNamedItem(
								String.format("input%dLabel",
										Integer.valueOf(i))) != null) {
							Labels.add(xElement
									.getAttributes()
									.getNamedItem(
											String.format("input%dLabel",
													Integer.valueOf(i)))
									.getNodeValue().toString());
						}
					}

					if (InputCount > 0 && Labels.size() == 0) {
						// if no labels provided add title as label
						Labels.add(Title);
					}
				} else if (Type == 7) {
					// call to number
					// nothing to read
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// } catch (IOException e) {
			// e.printStackTrace();
		}
	}

	List<CharSequence> getList() {
		adapterList.clear();
		for (Menu m : Submenus) {
			adapterList.add(m.Title);
		}
		return adapterList;
	}

}
