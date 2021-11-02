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
 *
 */

package io.olric.config;

import io.olric.hash64.Hash64;
import io.olric.hash64.impl.Hash64Impl;

public class Config {
    private int replicaCount;
    private Hash64 hash64;
    private double loadFactor;

    public void setReplicaCount(int replicaCount) {
        this.replicaCount = replicaCount;
    }

    public int getReplicaCount() {
        if (replicaCount == 0) {
            setReplicaCount(20);
        }
        return replicaCount;
    }

    public void setHash64(Hash64 hash64) {
        this.hash64 = hash64;
    }

    public Hash64 getHash64() {
        if (hash64 == null) {
            this.setHash64(new Hash64Impl());
        }
        return hash64;
    }

    public void setLoadFactor(double loadFactor) {
        this.loadFactor = loadFactor;
    }

    public double getLoadFactor() {
        if (this.loadFactor == 0) {
            this.loadFactor = 1.5;
        }
        return this.loadFactor;
    }

    public static Config getConfig() {
        return new Config();
    }
}
