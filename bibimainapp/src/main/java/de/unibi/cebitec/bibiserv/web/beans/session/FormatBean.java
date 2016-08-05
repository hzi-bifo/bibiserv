package de.unibi.cebitec.bibiserv.web.beans.session;

import de.unibi.cebitec.bibiserv.utils.SiblingGetter;
import de.unibi.techfak.bibiserv.util.ontoaccess.TypeOntoQuestioner;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.BiBiObjectProperty;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.OntoRepresentation;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.OntoAccessException;
import de.unibi.techfak.bibiserv.util.ontoaccess.bibiontotypes.impl.OntoRepresentationImplementation;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultMutableTreeNode;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.springframework.beans.factory.InitializingBean;

/**
 * This bean manages requests to the format overview page.
 *
 * @author Thomas Gatter - tgatter(at)techfak.uni-bielefeld.de
 */
public class FormatBean implements InitializingBean {
    
    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FormatBean.class);
    
    private ExtendedTreeNode formats, contents, datastructures;
    private ExtendedTreeNode selectedFormat, selectedContent, selectedDatastructure;
    private List<FormatData> representations;
    private FormatDataComparator comparator = new FormatDataComparator();
    private DefaultMutableTreeNodeComparator treeNodeComparator = new DefaultMutableTreeNodeComparator();

    @Override
    public void afterPropertiesSet() throws Exception {
        
        DefaultMutableTreeNode rawDefault = (DefaultMutableTreeNode) TypeOntoQuestioner.getAllFormats();
        Map<String,String> contentMap = new HashMap<>();
        contentMap.put("format", "All Formats");
        rawDefault.setUserObject(contentMap);
        DefaultMutableTreeNode newRoot = new DefaultMutableTreeNode();
        newRoot.add(rawDefault);
        formats = buildTree(newRoot);

        rawDefault = (DefaultMutableTreeNode) TypeOntoQuestioner.getAllContents();
        contentMap = new HashMap<>();
        contentMap.put("content", "All Contents");
        rawDefault.setUserObject(contentMap);
        newRoot = new DefaultMutableTreeNode();
        newRoot.add(rawDefault);
        contents = buildTree(newRoot);
        
        rawDefault = (DefaultMutableTreeNode) TypeOntoQuestioner.getAllDatastructures();
        contentMap = new HashMap<>();
        contentMap.put("datastructure", "All Datastructures");
        rawDefault.setUserObject(contentMap);
        newRoot = new DefaultMutableTreeNode();
        newRoot.add(rawDefault);
        datastructures = buildTree(newRoot);
        
        setSelectedContent((ExtendedTreeNode) contents.getChildren().get(0));
        setSelectedDatastructure((ExtendedTreeNode) datastructures.getChildren().get(0));
        setSelectedFormat((ExtendedTreeNode) formats.getChildren().get(0));
        
        filter();
    }

    // ---------------- Filtering -------------------
    
    public void onFormatSelect() {
        filter();
    }

    public void onContentSelect() {
        filter();
    }

    public void onDatastructureSelect() {
        filter();
    }

    /**
     * Called by filter trees.
     */
    private void filter() {

        log.fatal("Filter");
        
        String inputFormat = null;
        String inputContent = null;
        String inputDatastructure = null;

        List<BiBiObjectProperty> representations = new ArrayList<BiBiObjectProperty>();

        // Questioner need null if empty, but dropdowns provide ALLSTRING
        if (selectedFormat != null && !selectedFormat.getKey().equals("format")) {
            inputFormat = selectedFormat.getKey();
        }

        if (selectedContent != null && !selectedContent.getKey().equals("content")) {
            inputContent = selectedContent.getKey();
        }

        if (selectedDatastructure != null && !selectedDatastructure.getKey().equals("datastructure")) {
            inputDatastructure = selectedDatastructure.getKey();
        }

        log.fatal(inputFormat + " " + inputContent + " " +inputDatastructure);
        
        //  get data for format from OntoAccess
        Map<String, Collection> answerMap = TypeOntoQuestioner.
                getOtherInfoFrom_F_C_or_DS(inputFormat, inputContent,
                inputDatastructure);

        this.representations = representationToFormatData(answerMap.get("representations"));
    }
    
    private List<FormatData> representationToFormatData(Collection<BiBiObjectProperty> reps) {
        List<FormatData> formatData = new ArrayList<>();
        for(BiBiObjectProperty rep:reps){
            formatData.add(new FormatData(rep.getKey(), rep.getLabel()));
        }
        Collections.sort(formatData,comparator);
        return formatData;
    }

    public List<FormatData> getRepresentations() {
        return representations;
    }

    // ---------------- getter and Setter --------------------

    public ExtendedTreeNode getSelectedFormat() {
        return selectedFormat;
    }

    public void setSelectedFormat(ExtendedTreeNode selectedFormat) {
        if (selectedFormat != null) {
            this.selectedFormat = selectedFormat;
        }
    }

    public ExtendedTreeNode getSelectedContent() {
        return selectedContent;
    }

    public void setSelectedContent(ExtendedTreeNode selectedContent) {
        this.selectedContent = selectedContent;
    }

    public ExtendedTreeNode getSelectedDatastructure() {
        return selectedDatastructure;
    }

    public void setSelectedDatastructure(ExtendedTreeNode selectedDatastructures) {
        this.selectedDatastructure = selectedDatastructures;
    }

    public ExtendedTreeNode getFormats() {
        return formats;
    }

    public ExtendedTreeNode getContents() {
        return contents;
    }

    public ExtendedTreeNode getDatastructures() {
        return datastructures;
    }
    
    // ---------------- Helper Classes --------------------
    
    
    /**
     * Builds up a visualizeable tree from OntoAccess data.
     * @param node ontoAccess Node
     * @return full tree
     */
    private ExtendedTreeNode buildTree(DefaultMutableTreeNode node) {
        return (buildTree(node, null));
    }

    private ExtendedTreeNode buildTree(DefaultMutableTreeNode node, ExtendedTreeNode parent) { 
        /*
         * Please note, that the HashMap always contains just one element
         * carrying key and label of the given leaf.
         */
        HashMap<String, String> map = (HashMap<String, String>) node.getUserObject();

        ExtendedTreeNode output;

        if (map != null) {
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            Map.Entry<String, String> entry = iterator.next();
            String label = (String) entry.getValue();
            String key = (String) entry.getKey();
            output = new ExtendedTreeNode(key, label, parent);
        } else {
            output = new ExtendedTreeNode(null, null, parent);
        }

        Enumeration children = node.children();
        List<DefaultMutableTreeNode> childrenList= new ArrayList<>();
        
        while (children.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) children.nextElement();
            childrenList.add(child);
        }
        
        Collections.sort(childrenList,treeNodeComparator);
        
        for(DefaultMutableTreeNode child:childrenList) {         
            buildTree(child, output);
        }
        
        return output;
    }
    
    /**
     * This special TreeNode just adds a private String containing the key of
     * the displayed content. The key can only be initialized at the objects
     * construction and can not be changed later. In all other aspects it is
     * just a primefaces-DefaultTreeNode
     */
    public class ExtendedTreeNode extends DefaultTreeNode {

        private String key = "Error. Key value could not be retrieved";

        public ExtendedTreeNode(String key, String label, ExtendedTreeNode parent) {

            super(label, parent);

            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
    
    public class FormatData {

        private String key, label, convertableTo, streamConvertableTo, convertableFrom, streamConvertableFrom;
        private List<String> edamSynonyms;
        private boolean dataSet;
        
        public FormatData(String key, String label) {
            this.key = key;
            this.label = label;
            dataSet=false;
        }
        
        private void buildData(){
            try {
                OntoRepresentation rep = new OntoRepresentationImplementation(key);
                edamSynonyms = rep.getEdamSynonyms();
                convertableTo = buildConvertString(SiblingGetter.getSiblingsConvertableTo(rep));
                streamConvertableTo = buildConvertString(SiblingGetter.getSiblingsStreamConvertableTo(rep));
                
                convertableFrom = buildConvertString(SiblingGetter.getSiblingsConvertableFrom(rep));
                streamConvertableFrom = buildConvertString(SiblingGetter.getSiblingsStreamConvertableFrom(rep));
                log.fatal(rep.getKey());
                log.fatal(streamConvertableFrom);
                log.fatal(convertableFrom);
            } catch (URISyntaxException|OntoAccessException ex) {
                
            }
            dataSet = true;
        } 
        
        private String buildConvertString(List<OntoRepresentation> representations) {
            
            StringBuilder sup = new StringBuilder();
            boolean first = true;
            
            if(representations.isEmpty()) {
                sup.append("None");
            }
            
            for (OntoRepresentation representation : representations) {
                if (first) {
                    sup.append(representation.getFormatLabel());
                    first = false;
                } else {
                    sup.append(", ").append(representation.getFormatLabel());
                }
            }
            return sup.toString();
        }
        
        public String getKey() {
            return key;
        }

        public String getLabel() {
            return label;
        }

        public String getConvertableTo() {
            if(!dataSet) {
                buildData();
            }
            return convertableTo;
        }

        public String getStreamConvertableTo() {
            if(!dataSet) {
                buildData();
            }
            return streamConvertableTo;
        }

        public String getConvertableFrom() {
            if(!dataSet) {
                buildData();
            }
            return convertableFrom;
        }

        public String getStreamConvertableFrom() {
            if(!dataSet) {
                buildData();
            }
            return streamConvertableFrom;
        }

        public List<String> getEdamSynonyms() {
            if(!dataSet) {
                buildData();
            }

            return edamSynonyms;
        }

    }
    
    public static class FormatDataComparator implements Comparator<FormatData> {

        @Override
        public int compare(FormatData o1, FormatData o2) {
            return o1.getLabel().compareToIgnoreCase(o2.getLabel());
        }
    }
    
    public static class DefaultMutableTreeNodeComparator implements Comparator<DefaultMutableTreeNode> {

        @Override
        public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
            
            HashMap<String, String> map1 = (HashMap<String, String>) o1.getUserObject();
            HashMap<String, String> map2 = (HashMap<String, String>) o2.getUserObject();
            
            String o1String = "";
            String o2String = "";

            if (map1 != null) {
                Iterator<Map.Entry<String, String>> iterator = map1.entrySet().iterator();
                Map.Entry<String, String> entry = iterator.next();
                o1String = (String) entry.getValue();
            }
            if (map2 != null) {
                Iterator<Map.Entry<String, String>> iterator = map2.entrySet().iterator();
                Map.Entry<String, String> entry = iterator.next();
                o2String = (String) entry.getValue();
            } 
            
            return o1String.compareToIgnoreCase(o2String);
        }
    }
    
}
