//
// ========================================================================
// Copyright (c) 1995-2020 Mort Bay Consulting Pty Ltd and others.
//
// This program and the accompanying materials are made available under
// the terms of the Eclipse Public License 2.0 which is available at
// https://www.eclipse.org/legal/epl-2.0
//
// This Source Code may also be made available under the following
// Secondary Licenses when the conditions for such availability set
// forth in the Eclipse Public License, v. 2.0 are satisfied:
// the Apache License v2.0 which is available at
// https://www.apache.org/licenses/LICENSE-2.0
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
// ========================================================================
//

package org.eclipse.jetty.util.compression;

import java.util.zip.Inflater;

import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.thread.ThreadPool;

public class InflaterPool extends CompressionPool<Inflater>
{
    private final boolean nowrap;

    /**
     * Create a Pool of {@link Inflater} instances.
     * <p>
     * If given a capacity equal to zero the Inflaters will not be pooled
     * and will be created on acquire and ended on release.
     * If given a negative capacity equal to zero there will be no size restrictions on the InflaterPool
     *
     * @param capacity maximum number of Inflaters which can be contained in the pool
     * @param nowrap if true then use GZIP compatible compression for all new Inflater objects
     */
    public InflaterPool(int capacity, boolean nowrap)
    {
        super(capacity);
        this.nowrap = nowrap;
    }

    @Override
    protected Inflater newPooled()
    {
        return new Inflater(nowrap);
    }

    @Override
    protected void end(Inflater inflater)
    {
        inflater.end();
    }

    @Override
    protected void reset(Inflater inflater)
    {
        inflater.reset();
    }

    public static InflaterPool ensurePool(ContainerLifeCycle containerLifeCycle)
    {
        InflaterPool pool = containerLifeCycle.getBean(InflaterPool.class);
        if (pool != null)
            return pool;

        int capacity = CompressionPool.DEFAULT_CAPACITY;
        ThreadPool.SizedThreadPool threadPool = containerLifeCycle.getBean(ThreadPool.SizedThreadPool.class);
        if (threadPool != null)
            capacity = threadPool.getMaxThreads();

        pool = new InflaterPool(capacity, true);
        containerLifeCycle.addBean(pool);
        return pool;
    }
}
