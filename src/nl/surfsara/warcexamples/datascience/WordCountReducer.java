package nl.surfsara.warcexamples.datascience;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.io.*;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.mapreduce.ReduceContext;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.task.annotation.Checkpointable;

import java.io.IOException;
import java.util.*;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Created by naward on 11-7-15.
 */
//Reducer works with keys and values in textual format:
//public class Reducer<KEYIN, VALUEIN, KEYOUT, VALUEOUT> {
public class WordCountReducer extends Reducer<Text, Text, Text, Text> {

	@Override
    public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

    	int reducerUsed = 2;

    	//since we are using same mapper for both reducers 1st run is a job with no reducer
    	//Then we run 2 jobs with no mapper, but 2 different reduce functions

    	switch(reducerUsed){

    		//reducer 0: (no reducer)
    		case 0:

		        for (Text value : values) {
		        	context.write(key, value);
		        }

    			break;

    		//reducer 1: reduce domain's wordcount (avarage it out)
    		case 1:

                //list of words to exclude (html tags)
                //doing so might introduce errors in counts, but it is acceptable
                List<String> htmlTags = Arrays.asList(
                    "!DOCTYPE",
                    "a",
                    "abbr",
                    "address",
                    "area",
                    "article",
                    "aside",
                    "audio",
                    "b",
                    "base",
                    "bdi",
                    "bdo",
                    "blockquote",
                    "body",
                    "br",
                    "button",
                    "canvas",
                    "caption",
                    "cite",
                    "code",
                    "col",
                    "colgroup",
                    "data",
                    "datalist",
                    "dd",
                    "del",
                    "details",
                    "dfn",
                    "dialog",
                    "div",
                    "dl",
                    "dt",
                    "em",
                    "embed",
                    "fieldset",
                    "figcaption",
                    "figure",
                    "footer",
                    "form",
                    "h1",
                    "h2",
                    "h3",
                    "h4",
                    "h5",
                    "h6",
                    "head",
                    "header",
                    "hgroup",
                    "hr",
                    "html",
                    "i",
                    "iframe",
                    "img",
                    "input",
                    "ins",
                    "kbd",
                    "keygen",
                    "label",
                    "legend",
                    "li",
                    "link",
                    "main",
                    "map",
                    "mark",
                    "menu",
                    "menuitem",
                    "meta",
                    "meter",
                    "nav",
                    "noscript",
                    "object",
                    "ol",
                    "optgroup",
                    "option",
                    "output",
                    "p",
                    "param",
                    "pre",
                    "progress",
                    "q",
                    "rb",
                    "rp",
                    "rt",
                    "rtc",
                    "ruby",
                    "s",
                    "samp",
                    "script",
                    "section",
                    "select",
                    "small",
                    "source",
                    "span",
                    "strong",
                    "style",
                    "sub",
                    "summary",
                    "sup",
                    "table",
                    "tbody",
                    "td",
                    "template",
                    "textarea",
                    "tfoot",
                    "th",
                    "thead",
                    "time",
                    "title",
                    "tr",
                    "track",
                    "u",
                    "ul",
                    "var",
                    "video",
                    "wbr",
                    "hidden",
                    "high",
                    "href",
                    "hreflang",
                    "http-equiv",
                    "icon",
                    "id",
                    "ismap",
                    "itemprop",
                    "keytype",
                    "kind",
                    "label",
                    "lang",
                    "language",
                    "list",
                    "loop",
                    "low",
                    "manifest",
                    "max",
                    "maxlength",
                    "media",
                    "method",
                    "min",
                    "multiple",
                    "email",
                    "file",
                    "name",
                    "novalidate",
                    "open",
                    "optimum",
                    "pattern",
                    "ping",
                    "placeholder",
                    "poster",
                    "preload",
                    "pubdate",
                    "radiogroup",
                    "readonly",
                    "rel",
                    "required",
                    "reversed",
                    "rows",
                    "rowspan",
                    "sandbox",
                    "spellcheck",
                    "scope",
                    "scoped",
                    "seamless",
                    "selected",
                    "shape",
                    "size",
                    "type",
                    "text",
                    "password",
                    "sizes",
                    "span",
                    "src",
                    "srcdoc",
                    "srclang",
                    "srcset",
                    "start",
                    "step",
                    "style",
                    "summary",
                    "tabindex",
                    "target",
                    "title",
                    "type",
                    "usemap",
                    "value",
                    "width",
                    "canvas",
                    "wrap",
                    "border",
                    "buffered",
                    "challenge",
                    "charset",
                    "checked",
                    "cite",
                    "class",
                    "code",
                    "codebase",
                    "color",
                    "color",
                    "cols",
                    "colspan",
                    "content",
                    "http-equiv",
                    "name",
                    "contenteditable",
                    "contextmenu",
                    "controls",
                    "coords",
                    "data",
                    "datetime",
                    "default",
                    "defer",
                    "dir",
                    "dirname",
                    "disabled",
                    "download",
                    "draggable",
                    "dropzone",
                    "enctype",
                    "method",
                    "for",
                    "form",
                    "formaction",
                    "headers",
                    "height",
                    "accept",
                    "accept-charset",
                    "accesskey",
                    "action",
                    "align",
                    "alt",
                    "async",
                    "autocomplete",
                    "autofocus",
                    "autoplay",
                    "autosave",
                    "bgcolor",
                    "background-color",
                    "rgba",
                    "rgb",
                    "nbsp",
                    "com",
                    "http",
                    "www",
                    "px",
                    "tag",
                    "item",
                    "amp",
                    "type",
                    "display",
                    "block",
                    "https",
                    "this"
                );

    			//domain level hashmap and counter
    			int counter = 0;
    			HashMap<String, Integer> domainWordCount = new HashMap<String, Integer>();

    			for (Text value : values) {

    				//match mapped hashmaps
    				if(!value.toString().startsWith("Count:{"))
    					continue;

    				//else it does:
    				HashMap<String, Integer> tmp = WordCountMapper.parseToMap(value.toString());
    				
    				//iterate over next hashmap and combine it with domainWordCount
    				for(Map.Entry<String, Integer> e : tmp.entrySet()){

                        //if html word - skip
                        if(htmlTags.contains(e.getKey().toLowerCase()))
                            continue;

    					if(!domainWordCount.containsKey(e.getKey())){
			                //add to list
			                domainWordCount.put(e.getKey(), e.getValue());
			            } else {
			                //combine them together
			                int prevVal = 0;
                            if(domainWordCount.get(e.getKey()) != null){ //was causing NPtr Exception
                                prevVal = domainWordCount.get(e.getKey());
                            }
			                domainWordCount.put(e.getKey(), prevVal+e.getValue());
			            }
    				}

		        }

		        //average out the output map:
		        //the reason = there are samples of different sizes, we need to avg them out 
		        for(String _key : domainWordCount.keySet()){
		        	if(counter>0){
		            	domainWordCount.put(_key, domainWordCount.get(_key)/counter);
		            }
		        }

                //sort the map - we do this to take top 100 words only
                Map<String, Integer> sortedDomainWordCount = sortByComparator(domainWordCount);

		        //following would be normal output, modifying to match visualisation requirenments
		        //context.write(key, WordCountMapper.parseToString(domainWordCount));

		        StringBuilder sb = new StringBuilder();
		        sb.append("{url:\""+key+"\", words:[");

                int MAX_LEN = 100;
                int i = 0;

		        for(Map.Entry<String, Integer> e : sortedDomainWordCount.entrySet()){
		            if(i++ < MAX_LEN){
                        sb.append("{w:\""+e.getKey()+"\", c:"+e.getValue()+"},");
                    }
                }

		        sb.append("]}");

                //write:
                context.write(key, new Text(sb.toString()));

    			break;

    		//reducer 2: count links from domain1 to each of the domains it points out to
    		case 2:

    			//domain level hashmap to store outbound links from key node
    			HashMap<String, Integer> domainOutboundLinksCount = new HashMap<String, Integer>();

    			for (Text value : values) {

    				//skip if it is hashmap (wordcount)
    				if(value.toString().startsWith("Count:{"))
    					continue;

    				String outURL = value.toString();

    				//else proceed

    				//if not in hashmap, add it
    				if(!domainOutboundLinksCount.containsKey(outURL)){
		                //add to hashmap
		                domainOutboundLinksCount.put(outURL, 1);
		            } else {
		                //otherwise increment link count
		                domainOutboundLinksCount.put(outURL, domainOutboundLinksCount.get(outURL)+1);
		            }

    			}

                //normal MR code
    			//context.wrtie(key, domainOutboundLinksCount);

                //tailored for visulaisations MR code:
                for(Map.Entry<String, Integer> e : domainOutboundLinksCount.entrySet()){
                    String out = "{srce: \""+key+"\", dest:\"" + e.getKey()+"\", count:"+e.getValue()+"},";
                    context.write(key, new Text(out));
                }

    			break;
    	}

    }

    public static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap){

        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1,
                    Entry<String, Integer> o2)
            {
                //sort descending
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    /**
     * DECOMPILED PARENT:
     */
//    @Checkpointable
//    @InterfaceAudience.Public
//    @InterfaceStability.Stable
//    public class Reducer<KEYIN, VALUEIN, KEYOUT, VALUEOUT> {
//        public Reducer() {
//        }
//
//        protected void setup(Reducer.Context context) throws IOException, InterruptedException {
//        }
//
//        protected void reduce(KEYIN key, Iterable<VALUEIN> values, Reducer.Context context) throws IOException, InterruptedException {
//            Iterator i$ = values.iterator();
//
//            while(i$.hasNext()) {
//                Object value = i$.next();
//                context.write(key, value);
//            }
//
//        }
//
//        protected void cleanup(Reducer.Context context) throws IOException, InterruptedException {
//        }
//
//        public void run(Reducer.Context context) throws IOException, InterruptedException {
//            this.setup(context);
//
//            try {
//                while(context.nextKey()) {
//                    this.reduce(context.getCurrentKey(), context.getValues(), context);
//                    Iterator iter = context.getValues().iterator();
//                    if(iter instanceof ReduceContext.ValueIterator) {
//                        ((ReduceContext.ValueIterator)iter).resetBackupStore();
//                    }
//                }
//            } finally {
//                this.cleanup(context);
//            }
//
//        }
//
//        public abstract class Context implements ReduceContext<KEYIN, VALUEIN, KEYOUT, VALUEOUT> {
//            public Context() {
//            }
//        }
//    }
}
