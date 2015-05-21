/*
 * Copyright 2014 dc-square GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hivemq.spi.topic;

import com.hivemq.spi.message.QoS;
import com.hivemq.spi.topic.MqttTopicPermission.ACTIVITY;
import com.hivemq.spi.topic.MqttTopicPermission.QOS;
import org.junit.Test;

import static com.hivemq.spi.topic.MqttTopicPermission.*;
import static org.junit.Assert.*;


/**
 * @author Dominik Obermaier
 */
public class MqttTopicPermissionTest {

    @Test
    public void testQualityOfService() throws Exception {

        final MqttTopicPermission all = new MqttTopicPermission("testtopic", TYPE.ALLOW, QOS.ALL);
        assertTrue(all.implies(all));

        final MqttTopicPermission one = new MqttTopicPermission("testtopic", TYPE.ALLOW, QOS.ONE);
        assertTrue(one.implies(one));

        assertTrue(all.implies(one));
        assertFalse(one.implies(all));

        final MqttTopicPermission two = new MqttTopicPermission("testtopic", TYPE.ALLOW, QOS.TWO);
        assertTrue(two.implies(two));

        assertTrue(all.implies(two));
        assertFalse(two.implies(all));

        assertFalse(two.implies(one));
        assertFalse(one.implies(two));

        final MqttTopicPermission zero = new MqttTopicPermission("testtopic", TYPE.ALLOW, QOS.ZERO);
        assertTrue(zero.implies(zero));

        assertTrue(all.implies(zero));
        assertFalse(zero.implies(all));

        assertFalse(one.implies(zero));
        assertFalse(zero.implies(one));

        assertFalse(two.implies(zero));
        assertFalse(zero.implies(two));

        final MqttTopicPermission one_two = new MqttTopicPermission("testtopic", TYPE.ALLOW, QOS.ONE_TWO);
        assertTrue(one_two.implies(one_two));

        assertTrue(all.implies(one_two));
        assertFalse(one_two.implies(all));

        assertTrue(one_two.implies(one));
        assertFalse(one.implies(one_two));

        assertTrue(one_two.implies(two));
        assertFalse(two.implies(one_two));

        assertFalse(one_two.implies(zero));
        assertFalse(zero.implies(one_two));

        final MqttTopicPermission zero_one = new MqttTopicPermission("testtopic", TYPE.ALLOW, QOS.ZERO_ONE);
        assertTrue(zero_one.implies(zero_one));

        assertTrue(all.implies(zero_one));
        assertFalse(zero_one.implies(all));

        assertTrue(zero_one.implies(one));
        assertFalse(one.implies(zero_one));

        assertFalse(zero_one.implies(two));
        assertFalse(two.implies(zero_one));

        assertTrue(zero_one.implies(zero));
        assertFalse(zero.implies(zero_one));

        assertFalse(zero_one.implies(one_two));
        assertFalse(one_two.implies(zero_one));

        final MqttTopicPermission zero_two = new MqttTopicPermission("testtopic", TYPE.ALLOW, QOS.ZERO_TWO);
        assertTrue(zero_two.implies(zero_two));

        assertFalse(zero_two.implies(one));
        assertFalse(one.implies(zero_two));

        assertTrue(zero_two.implies(two));
        assertFalse(two.implies(zero_two));

        assertTrue(zero_two.implies(zero));
        assertFalse(zero.implies(zero_two));

        assertFalse(zero_two.implies(zero_one));
        assertFalse(zero_one.implies(zero_two));

        assertFalse(zero_two.implies(one_two));
        assertFalse(one_two.implies(zero_two));

    }

    @Test
    public void testAllowedActivity() {

        final MqttTopicPermission p1 = new MqttTopicPermission("testtopic", TYPE.ALLOW, ACTIVITY.ALL);
        assertTrue(p1.implies(p1));

        final MqttTopicPermission p2 = new MqttTopicPermission("testtopic", TYPE.ALLOW, ACTIVITY.ALL);
        assertTrue(p2.implies(p2));

        assertTrue(p1.implies(p2));
        assertTrue(p2.implies(p1));

        final MqttTopicPermission p3 = new MqttTopicPermission("testtopic", TYPE.ALLOW, ACTIVITY.PUBLISH);
        assertTrue(p3.implies(p3));

        assertTrue(p1.implies(p3));
        assertFalse(p3.implies(p1));

        final MqttTopicPermission p4 = new MqttTopicPermission("testtopic", TYPE.ALLOW, ACTIVITY.SUBSCRIBE);
        assertTrue(p4.implies(p4));

        assertTrue(p1.implies(p4));
        assertFalse(p4.implies(p1));

        assertFalse(p4.implies(p3));
        assertFalse(p3.implies(p4));

    }

    @Test
    public void testTopicImplicity() {
        final MqttTopicPermission t1 = new MqttTopicPermission("testtopic", TYPE.ALLOW);
        assertTrue(t1.implies(t1));

        //Has nothing to do with t1
        final MqttTopicPermission t2 = new MqttTopicPermission("testtopic1", TYPE.ALLOW);

        assertTrue(t2.implies(t2));

        assertFalse(t2.implies(t1));
        assertFalse(t1.implies(t2));


        //test subtopic without wildcards
        final MqttTopicPermission t3 = new MqttTopicPermission("testtopic/subtopic", TYPE.ALLOW);

        assertTrue(t3.implies(t3));

        assertFalse(t1.implies(t3));
        assertFalse(t3.implies(t1));

        assertFalse(t2.implies(t3));
        assertFalse(t3.implies(t2));


        //Test wildcard
        final MqttTopicPermission t4 = new MqttTopicPermission("testtopic/#", TYPE.ALLOW);

        assertTrue(t4.implies(t4));

        assertTrue(t4.implies(t1));
        assertFalse(t1.implies(t4));

        assertTrue(t4.implies(t3));
        assertFalse(t3.implies(t4));

        final MqttTopicPermission t5 = new MqttTopicPermission("testtopic/+", TYPE.ALLOW);

        assertTrue(t5.implies(t5));

        assertFalse(t5.implies(t1));
        assertFalse(t1.implies(t5));

        assertTrue(t5.implies(t3));
        assertFalse(t3.implies(t5));

        final MqttTopicPermission t6 = new MqttTopicPermission("testtopic/subtopic/again", TYPE.ALLOW);

        assertFalse(t5.implies(t6));
        assertFalse(t6.implies(t5));

        assertTrue(t4.implies(t6));
        assertFalse(t6.implies(t4));

        final MqttTopicPermission t7 = new MqttTopicPermission("#", TYPE.ALLOW);

        assertTrue(t7.implies(t7));

        assertTrue(t7.implies(t6));
        assertFalse(t6.implies(t7));

        assertTrue(t7.implies(t5));
        assertFalse(t5.implies(t7));

        assertTrue(t7.implies(t4));
        assertFalse(t4.implies(t7));

    }

    @Test
    public void test_no_mqtt_topic_permission() throws Exception {
        final MqttTopicPermission mqttTopicPermission = new MqttTopicPermission("test", TYPE.ALLOW);
        assertFalse(mqttTopicPermission.implies(null));
    }


    @Test
    public void test_allowed_qos_at_most_once() throws Exception {
        assertEquals(QOS.from(QoS.AT_MOST_ONCE), QOS.ZERO);
    }

    @Test
    public void test_allowed_qos_exactly_once() throws Exception {
        assertEquals(QOS.from(QoS.EXACTLY_ONCE), QOS.TWO);
    }

    @Test
    public void test_allowed_qos_at_least_once() throws Exception {
        assertEquals(QOS.from(QoS.AT_LEAST_ONCE), QOS.ONE);
    }

    @Test
    public void test_implies_params_null() throws Exception {

        final MqttTopicPermission t1 = new MqttTopicPermission("#", TYPE.ALLOW, QOS.ALL, ACTIVITY.ALL);

        assertFalse(t1.implies(null, QoS.AT_LEAST_ONCE, ACTIVITY.PUBLISH));

        //hack for Qos to be null
        assertFalse(t1.implies("test", QoS.valueOf(4), ACTIVITY.PUBLISH));

        assertFalse(t1.implies("test", QoS.AT_LEAST_ONCE, null));
    }
}
