package com.sanjiv.samples.zkutils;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;

public enum ZkUtil {
    instance;
    CuratorFramework client;
    
    private ZkUtil() {
	init();
    }
    
    private void init () {
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
    
    public void create(String path, String payload) {
	try {
	    if (client.checkExists().forPath(path) == null) {
		client.create().creatingParentsIfNeeded().forPath(path);
	    }
	    if (null != payload) {
		client.setData().forPath(path, payload.getBytes());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}    
    }
    
    public void createEphermal(String path, String payload) {
	try {
	    if (client.checkExists().forPath(path) == null) {
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(path);
	    }
	    if (null != payload) {
		client.setData().forPath(path, payload.getBytes());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}    
    }
    
    public String sendToQueue(String queue, String payload) {
	
	String str = "";
	try {
	    str = client.create().creatingParentsIfNeeded().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL).forPath(queue, payload.getBytes());
	    
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return str;
    }
    
   
    
    public List<String> listenToQueue(String queueName) throws Exception {
	return client.getChildren().forPath(queueName).stream().sorted().map(s -> {
	    	String str ="";
        	try { str = new String(client.getData().forPath(queueName+"/"+s)); 
        	} catch (Exception e) {
        	    e.printStackTrace();
        	}
        	return str;
	}).collect(Collectors.toList());

    }
    
    public static void main(String[] args) {
	ZkUtil.instance.create("/examples/test/testNode", "This is test node");
	//String queue = ZkUtil.instance.createQueue("/examples/queues/testQueue", "This is distributed queue:");
	ZkUtil.instance.sendToQueue("/examples/queues/testQueue", "100");
	ZkUtil.instance.sendToQueue("/examples/queues/testQueue", "200");
	ZkUtil.instance.sendToQueue("/examples/queues/testQueue", "300");
	
	try {
	    ZkUtil.instance.listenToQueue("/examples/queues").forEach(System.out::println);;

	    Thread.sleep(5000);

	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	
	    
    }

}
