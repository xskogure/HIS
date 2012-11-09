package jp.ac.shizuoka.inf.cs.kogure.HIS;

import java.util.ArrayList;
import java.util.HashMap;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;

public class OntologyBase {
	String name;
	private ItemInfo rootNode;
	HashMap<String, Info>        infoHashMap;
	ArrayList<Info>              infoArrayList;
	HashMap<String, ItemInfo>    itemInfoHashMap;
	ArrayList<ItemInfo>          itemInfoArrayList;
	HashMap<String, AtomInfo>    atomInfoHashMap;
	ArrayList<AtomInfo>          atomInfoArrayList;
	HashMap<String, SubtypeInfo> subtypeInfoHashMap;
	ArrayList<SubtypeInfo>       subtypeInfoArrayList;
	HashMap<String, DataInfo>    dataInfoHashMap;
	ArrayList<DataInfo>          dataInfoArrayList;
	
	OntologyBase(){
		infoHashMap          = new HashMap<String, Info>();
		infoArrayList        = new ArrayList<Info>(); 
		itemInfoHashMap      = new HashMap<String, ItemInfo>();
		itemInfoArrayList    = new ArrayList<ItemInfo>(); 
		atomInfoHashMap      = new HashMap<String,AtomInfo>();    
		atomInfoArrayList    = new ArrayList<AtomInfo>();
		subtypeInfoHashMap   = new HashMap<String,SubtypeInfo>();    
		subtypeInfoArrayList = new ArrayList<SubtypeInfo>();
		dataInfoHashMap      = new HashMap<String,DataInfo>();    
		dataInfoArrayList    = new ArrayList<DataInfo>();
	}
	
	OntologyBase(String file){
		infoHashMap          = new HashMap<String, Info>();
		infoArrayList        = new ArrayList<Info>(); 
		itemInfoHashMap      = new HashMap<String, ItemInfo>();
		itemInfoArrayList    = new ArrayList<ItemInfo>(); 
		atomInfoHashMap      = new HashMap<String,AtomInfo>();    
		atomInfoArrayList    = new ArrayList<AtomInfo>();
		subtypeInfoHashMap   = new HashMap<String,SubtypeInfo>();    
		subtypeInfoArrayList = new ArrayList<SubtypeInfo>();
		dataInfoHashMap      = new HashMap<String,DataInfo>();    
		dataInfoArrayList    = new ArrayList<DataInfo>();
		loadOntology(file);
		
	}
	public void loadOntology(String file){		
		try {
            // DOMパーサ用ファクトリの生成
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // DOM Documentインスタンス用ファクトリの生成
            DocumentBuilder builder = factory.newDocumentBuilder();
            // エラー・ハンドラの登録
            builder.setErrorHandler(new MyHandler());      
            Document doc = builder.parse(file);
            rootNode = makeRootItemNode(doc);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public ItemInfo makeRootItemNode(Document doc){
		Element elem = doc.getDocumentElement();
		ItemInfo item = null;
		if (elem.getTagName().equals("OntologyBase")){
			name = elem.getAttribute("name");
			item = makeItemInfo(null, elem.getElementsByTagName("ItemInfo").item(0));
		}else{
			System.err.println("OntologyBase not found\n" + doc);
		}
		return item;
	}
	
	public ItemInfo makeItemInfo(SubtypeInfo p, Node elem){
		if (elem.hasAttributes()){
			String tagName = elem.getAttributes().getNamedItem("name").getNodeValue();
			ItemInfo it = new ItemInfo(p, tagName);
			if (elem.getNodeName().equals("ItemInfo")){
				NodeList list = elem.getChildNodes();
				for (int i=0; i<list.getLength(); i++){
					Node el = list.item(i);
					if (el.getNodeName().equals("SubtypeInfo")){
						SubtypeInfo st = makeSubtypeInfo(it, el);
						it.setChild(st);
					}
				}
			}else{
				System.err.println("ItemInfo not found\n" + elem);
			}
			setInfo(it);
			return it;
		}
		return null;
	}

	public SubtypeInfo makeSubtypeInfo(ItemInfo p, Node elem){
		String tagName = elem.getAttributes().getNamedItem("name").getNodeValue();
		SubtypeInfo st = new SubtypeInfo(p, tagName);
		if (elem.getNodeName().equals("SubtypeInfo")){
			NodeList list = elem.getChildNodes();
			for (int i=0; i<list.getLength(); i++){
				Node el = list.item(i);
				if (el.getNodeName().equals("ItemInfo")){
					ItemInfo it = makeItemInfo(st, el);
					st.setChild(it);
				}else if (el.getNodeName().equals("AtomInfo")){
					AtomInfo at = makeAtomInfo(st, el);
					st.setChild(at);
				}
			}
		}else{
			System.err.println("SubtypeInfo not found\n" + elem);
		}
		setInfo(st);
		return st;
	}

	public AtomInfo makeAtomInfo(SubtypeInfo p, Node elem){
		String tagName = elem.getAttributes().getNamedItem("name").getNodeValue();
		AtomInfo at = new AtomInfo(tagName);
		if (elem.getNodeName().equals("AtomInfo")){
			NodeList list = elem.getChildNodes();
			for (int i=0; i<list.getLength(); i++){
				Node el = list.item(i);
				if (el.getNodeName().equals("Data")){
					DataInfo di = new DataInfo(at, el.getAttributes().getNamedItem("name").getNodeValue());
					setInfo(di);
					at.setChild(di);
				}
			}
		}else{
			System.err.println("ItemInfo not found\n" + elem);
		}
		setInfo(at);
		return at;
	}

	public ItemInfo getRootNode() {return rootNode; }
	
	public void setInfo(Info info){
		infoHashMap.put(info.getName(), info);
		infoArrayList.add(info);
		if (info.isItemInfo()   ) setItemInfo((ItemInfo) info);
		if (info.isSubtypeInfo()) setSubtypeInfo((SubtypeInfo) info);
		if (info.isAtomInfo())    setAtomInfo((AtomInfo) info);
		if (info.isDataInfo())    setDataInfo((DataInfo) info);
	}
	
	public void setItemInfo(ItemInfo info){
		itemInfoHashMap.put(info.getName(), info);
		itemInfoArrayList.add(info);
	}
	
	public void setSubtypeInfo(SubtypeInfo info){
		subtypeInfoHashMap.put(info.getName(), info);
		subtypeInfoArrayList.add(info);
	}
	
	public void setAtomInfo(AtomInfo info){
		atomInfoHashMap.put(info.getName(), info);
		atomInfoArrayList.add(info);
	}
	
	public void setDataInfo(DataInfo info){
		dataInfoHashMap.put(info.getName(), info);
		dataInfoArrayList.add(info);
	}

	public int getDataNum(){
		return dataInfoArrayList.size();
	}
	
	public void message(String str){
		System.err.println(str);
	}
}

class MyHandler implements ErrorHandler {
    public void warning(SAXParseException e) {
        System.out.println("警告: " + e.getLineNumber() +"行目");
        System.out.println(e.getMessage());
    }
    public void error(SAXParseException e) {
        System.out.println("エラー: " + e.getLineNumber() +"行目");
        System.out.println(e.getMessage());
    }
    public void fatalError(SAXParseException e) {
        System.out.println("深刻なエラー: " + e.getLineNumber() +"行目");
        System.out.println(e.getMessage());
    }
}
