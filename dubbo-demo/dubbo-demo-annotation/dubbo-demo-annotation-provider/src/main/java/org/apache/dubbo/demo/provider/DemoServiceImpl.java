/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.demo.provider;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.apache.dubbo.config.annotation.Service;
import org.apache.dubbo.demo.DemoService;
import org.apache.dubbo.rpc.RpcContext;

import org.apache.dubbo.rpc.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service(registry = {"zookeeperConfig","nacosConfig"})
public class DemoServiceImpl implements DemoService {
    private static final Logger logger = LoggerFactory.getLogger(DemoServiceImpl.class);

    @PostConstruct
    public void init(){
         List<FlowRule> rules = new ArrayList<>();
        FlowRule flowRule = new FlowRule();
        flowRule.setCount(2).setGrade(RuleConstant.FLOW_GRADE_QPS)
                .setStrategy(RuleConstant.STRATEGY_DIRECT)
                .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT)
                /*.setLimitApp(RuleConstant.LIMIT_APP_DEFAULT)*/.setResource("org.apache.dubbo.demo.DemoService:sayHello(java.lang.String)")
                .as(FlowRule.class);
        rules.add(flowRule);
        FlowRuleManager.loadRules(rules);
    }

    @Override
    @SentinelResource(value = "sayHello", fallback = "helloFallback")
    public String sayHello(String name) {
        /*try(Entry entry = SphU.entry("sayHello")) {
            System.out.println(111111111+"-----------");
            entry.exit();
        }catch (BlockException b){
            System.out.println("被限流了------------");


        }*/
        //logger.info("Hello " + name + ", request from consumer: " + RpcContext.getContext().getRemoteAddress());
        return "Hello " + name + ", response from provider: " + RpcContext.getContext().getLocalAddress();
    }

    public String helloFallback(String s, Throwable ex) {
        // Do some log here.
        ex.printStackTrace();
        return "Oops, error occurred at " + s;
    }

    @Override
    public CompletableFuture<String> sayHelloAsync(String name) {
        return null;
    }

}
