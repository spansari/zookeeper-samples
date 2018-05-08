package com.sanjiv.samples.zkutils;

import java.io.IOException;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.framework.recipes.queue.BlockingQueueConsumer;
import org.apache.curator.framework.recipes.queue.DistributedQueue;
import org.apache.curator.framework.recipes.queue.QueueBuilder;
import org.apache.curator.framework.recipes.queue.QueueConsumer;
import org.apache.curator.framework.recipes.queue.QueueSerializer;
import org.apache.curator.framework.recipes.queue.SimpleDistributedQueue;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.RetryNTimes;

public enum DistributedQueueUtil {
    instance;
    
    CuratorFramework client;
   
    
    private DistributedQueueUtil() {
	init();
    }

    private void init() {
	client = CuratorFrameworkFactory.newClient("localhost:2181", new RetryNTimes(5, 1000));
	
	client.getCuratorListenable().addListener(new CuratorListener() {
		@Override
		public void eventReceived(CuratorFramework client,
				CuratorEvent event) throws Exception {
			System.out.println("CuratorEvent: "
					+ event.getType().name());
		}
	});
	client.start();
    }
    
    private static QueueSerializer<String> createQueueSerializer() {
	return new QueueSerializer<String>() {

		@Override
		public byte[] serialize(String item) {
			return item.getBytes();
		}

		@Override
		public String deserialize(byte[] bytes) {
			return new String(bytes);
		}

	};
}

    private static QueueConsumer<String> createQueueConsumer() {

	return new QueueConsumer<String>() {

		@Override
		public void stateChanged(CuratorFramework client,
				ConnectionState newState) {
			System.out.println("connection new state: " + newState.name());
		}

		@Override
		public void consumeMessage(String message) throws Exception {
			System.out.println("consume one message: " + message);
		}

	};
}
    
    public DistributedQueue<String> buildQueue(String queueName) {
	QueueConsumer<String> consumer = createQueueConsumer();
	QueueBuilder<String> builder = QueueBuilder.builder(client, consumer, createQueueSerializer(), queueName);
	DistributedQueue<String> queue = builder.buildQueue();
	try {
	    queue.start();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	
	return queue;
    }
    
    public void sendToQueue(DistributedQueue<String> queue, String payload) {
	try {
	    queue.put(payload);
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    try {
		queue.close();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }
    

    public static void main(String[] args) {
	DistributedQueue<String> queue = DistributedQueueUtil.instance.buildQueue("/examples/test/queue1");
	
	DistributedQueueUtil.instance.sendToQueue(queue, "Message-1");


	try {
	    Thread.sleep(5000);
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
	SimpleDistributedQueue sQueue = new SimpleDistributedQueue(DistributedQueueUtil.instance.client, "/examples/test/queue2");
	
	try {
	    sQueue.offer("111111".getBytes());
	    sQueue.offer("222222".getBytes());
	    
	    byte[] bytes;
	    while(   (bytes = sQueue.take() )!= null) {
		System.out.println(new String(bytes));
	    }
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
    }

}
