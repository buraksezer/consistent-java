/*
 * Copyright (c) 2021 Burak Sezer
 * All rights reserved.
 *
 * This code is licensed under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files(the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions :
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package io.olric;

import io.olric.config.Config;
import io.olric.exceptions.EmptyHashRingException;
import io.olric.exceptions.MemberNotFoundException;
import io.olric.member.Member;
import io.olric.member.impl.MemberImpl;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ConsistentTest {
    private ArrayList<Member> createMembers(Consistent c, int numMembers) {
        ArrayList<Member> members = new ArrayList<>();
        for (int i = 1; i <= numMembers; i++) {
            Member m = new MemberImpl(String.format("node%d.olric.io", i));
            c.addMember(m);
            members.add(m);
        }

        return members;
    }

    @Test(expected = EmptyHashRingException.class)
    public void createEmptyConsistentInstanceWithDefaultConfig() {
        Consistent c = new Consistent(Config.getConfig());
        c.locate("foobar");
    }

    @Test
    public void locateOneWithOneMember() {
        Consistent c = new Consistent(Config.getConfig());

        Member m = new MemberImpl("node1.olric.io");
        c.addMember(m);

        Member located = c.locate("foobar");
        assertEquals(m, located);
    }

    @Test
    public void locateManyWithOneMember() {
        Consistent c = new Consistent(Config.getConfig());

        Member m = new MemberImpl("node1.olric.io");
        c.addMember(m);

        for (int i = 0; i < 100; i++) {
            Member located = c.locate(String.format("foobar-%d", i));
            assertEquals(m, located);
        }
    }

    @Test
    public void incrAndGetLoadWithOneMember() {
        Consistent c = new Consistent(Config.getConfig());

        Member m = new MemberImpl("node1.olric.io");
        c.addMember(m);

        for (int i = 0; i < 100; i++) {
            Member located = c.locate(String.format("foobar-%d", i));
            c.incrLoad(located);
        }

        assertEquals((Integer) 100, c.getLoad(m));
        assertTrue(c.getLoad(m) < c.averageLoad());
    }

    @Test
    public void incrAndGetLoadWithManyMember() {
        Consistent c = new Consistent(Config.getConfig());

        Member m1 = new MemberImpl("node1.olric.io");
        c.addMember(m1);

        Member m2 = new MemberImpl("node2.olric.io");
        c.addMember(m2);

        Member m3 = new MemberImpl("node3.olric.io");
        c.addMember(m3);

        for (int i = 0; i < 100; i++) {
            Member located = c.locate(String.format("foobar-%d", i));
            c.incrLoad(located);
        }

        assertTrue(c.getLoad(m1) < 100);
        assertTrue(c.getLoad(m1) < c.averageLoad());
    }

    @Test
    public void incrDecrAndGetLoadWithManyMember() {
        Consistent c = new Consistent(Config.getConfig());
        int numMembers = 3;
        ArrayList<Member> members = createMembers(c, numMembers);

        for (int i = 0; i < 100; i++) {
            Member located = c.locate(String.format("foobar-%d", i));
            c.incrLoad(located);

            // do something with the selected node
            c.decrLoad(located);
        }

        for (int i = 1; i <= numMembers; i++) {
            Member m = members.get(i - 1);
            assertEquals((Integer) 0, c.getLoad(m));
            assertTrue(c.getLoad(m) < c.averageLoad());
        }
    }

    @Test
    public void getMembers() {
        Consistent c = new Consistent(Config.getConfig());
        int numMembers = 3;
        ArrayList<Member> members = createMembers(c, numMembers);

        assertEquals(members, c.members());
    }

    @Test
    public void checkSize() {
        Consistent c = new Consistent(Config.getConfig());
        int numMembers = 3;
        createMembers(c, numMembers);
        assertEquals(numMembers, c.size());
    }

    @Test
    public void removeMemberAndCheckSize() {
        Consistent c = new Consistent(Config.getConfig());
        int numMembers = 3;
        ArrayList<Member> members = createMembers(c, numMembers);

        for (Member member : members) {
            c.removeMember(member);
        }

        assertEquals(0, c.size());
    }

    @Test
    public void removeMemberAndAverageLoad() {
        Consistent c = new Consistent(Config.getConfig());
        int numMembers = 3;
        ArrayList<Member> members = createMembers(c, numMembers);

        for (Member member : members) {
            c.removeMember(member);
        }

        assertEquals(0, c.averageLoad(), 0.0);
    }

    @Test(expected = MemberNotFoundException.class)
    public void incrLoadMemberNotFound() {
        Consistent c = new Consistent(Config.getConfig());
        Member m = new MemberImpl("node1.olric.io");
        c.incrLoad(m);
    }

    @Test(expected = MemberNotFoundException.class)
    public void decrLoadMemberNotFound() {
        Consistent c = new Consistent(Config.getConfig());
        Member m = new MemberImpl("node1.olric.io");
        c.decrLoad(m);
    }

    @Test()
    public void minimumLoad() {
        Consistent c = new Consistent(Config.getConfig());
        Member m = new MemberImpl("node1.olric.io");
        c.addMember(m);
        c.decrLoad(m);

        Integer load = c.getLoad(m);
        assertEquals((Integer) 0, load);
    }
}
