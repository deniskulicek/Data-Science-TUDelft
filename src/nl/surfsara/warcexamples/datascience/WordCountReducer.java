package nl.surfsara.warcexamples.datascience;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.mapreduce.ReduceContext;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.task.annotation.Checkpointable;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by naward on 11-7-15.
 */
public class WordCountReducer  extends Reducer{
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
