package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Deferred;
import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tools.Tool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

    public class ManTask extends Task<Product> {

        private Product product;
        private Warehouse warehouse;

        public ManTask(Product product, Warehouse warehouse) {
            this.product = product;
            this.warehouse = warehouse;
        }

        /**
         * start for manufactoringPlan class
         */
        protected void start()
        {
            ManufactoringPlan plan = warehouse.getPlan(product.getName());

            if (plan.getParts().length > 0) {

                List<ManTask> manufacturedTasks = manfactureParts(plan.getParts());

                whenResolved(manufacturedTasks, () -> {
                            long allValues = calcFinalIdOfTools(plan.getTools());
                            product.setFinalId(product.getStartId() + allValues);
                            complete(product);
                        }
                );
            }
            else
            {
                product.setFinalId(product.getStartId());
                complete(product);
            }
        }

        /**
         *
         * @param tools
         * @return manufacturingTasks
         */
        private long calcFinalIdOfTools(String[] tools)
        {
            long finalId = 0;
            List<Long> toolResults = new ArrayList<>();
            CountDownLatch countDownLatch = new CountDownLatch(tools.length);

            for (String toolType : tools)
            {
                Deferred<Tool> toolDeferred = warehouse.acquireTool(toolType);

                toolDeferred.whenResolved(() -> {
                    toolResults.add(toolDeferred.get().useOn(product));
//                    warehouse.releaseTool(toolDeferred.get());
                    ToolTask toolTask=new ToolTask(warehouse,toolDeferred.get());
                    spawn(toolTask);
                    countDownLatch.countDown();
                });
            }

            try
            {
                countDownLatch.await();
            }
            catch (InterruptedException ignored) {}

            for (long toolResult : toolResults)
            {
                finalId+= toolResult;
            }

            return finalId;
        }

        /**
         *
         * @param plans
         * @return manufacturingTasks
         */
        private List<ManTask> manfactureParts(String[] plans)
        {
            List<ManTask> manufacturingTasks = new ArrayList<>();

            for (String partName : plans) {
                Product part = new Product(product.getStartId()+1, partName);
                product.addPart(part);
                ManTask manufacturingTask = new ManTask(part, warehouse);
                spawn(manufacturingTask);
                manufacturingTasks.add(manufacturingTask);
            }

            return manufacturingTasks;
        }
    }

