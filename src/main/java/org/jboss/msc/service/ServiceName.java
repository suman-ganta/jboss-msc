/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.msc.service;

/**
 * Service name class.
 *
 * @author John Bailey
 */
public final class ServiceName {
    private final String name;
    private final ServiceName parent;
    private final int hashCode;

    /**
     * The root name "jboss".
     */
    public static final ServiceName JBOSS = new ServiceName(null, "jboss");

    /**
     * Create a ServiceName from a series of String parts.
     *
     * @param parts The string representations of the service name segments
     * @return A ServiceName instance 
     */
    public static ServiceName of(final String... parts) {
        return of(null, parts);
    }

    /**
     * Create a ServiceName from a series of String parts and a parent service name.
     *
     * @param parent The parent ServiceName for this name
     * @param parts The string representations of the service name segments
     * @return A ServiceName instance
     */
    public static ServiceName of(final ServiceName parent, String... parts) {
        if(parts.length < 1)
            throw new IllegalArgumentException("Must provide at least one name segment");
        
        ServiceName current = parent;
        for(String part : parts)
            current = new ServiceName(current, part);
        return current;
    }

    private ServiceName(final ServiceName parent, final String name) {
        if (name == null) {
            throw new IllegalArgumentException("name is null");
        }
        this.name = name;
        this.parent = parent;

        int result = parent == null ? 1 : parent.hashCode();
        result = 31 * result + name.hashCode();
        hashCode = result;
    }

    /**
     * Create a service name by appending name parts using this as a parent to the new ServiceName.
     *
     * @param parts The parts to append
     * @return A new ServiceName
     */
    public ServiceName append(final String... parts) {
        return of(this, parts);
    }

    /**
     * Create a service name by appending name parts of the provided ServiceName using this as a parent.
     *
     * @param serviceName The service name to use as the parts to append
     * @return A new ServiceName
     */
    public ServiceName append(final ServiceName serviceName) {
        return of(this, getNameParts(serviceName, 0));
    }

    private String[] getNameParts(final ServiceName serviceName, final int idx) {
        final String[] array;
        if (serviceName.parent == null) {
            array = new String[idx + 1];
        } else {
            array = getNameParts(serviceName.parent, idx + 1);
        }
        array[array.length - 1 - idx] = serviceName.name;
        return array;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof ServiceName && equals((ServiceName)o);
    }

    public boolean equals(ServiceName o) {
        if (o == this) {
            return true;
        }
        if (o == null || hashCode != o.hashCode || ! name.equals(o.name)) {
            return false;
        }

        final ServiceName parent = this.parent;
        final ServiceName oparent = o.parent;
        return parent != null && parent.equals(oparent) || oparent == null;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return parent != null ? parent.toString() + "." + name : name;
    }
}