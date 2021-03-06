/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.common;

import org.drools.core.spi.Activation;
import org.drools.core.spi.AgendaGroup;

public interface InternalAgendaGroup extends AgendaGroup {
    public Activation getNext();
    
    public void add(Activation activation);
    
    public void setActive(boolean activate);
    
    public Activation  peekNext();
    
    public void clear();
    
    public Activation[] getAndClear();

    public void remove(Activation activation);

    public void setActivatedForRecency(long recency);
    
    public long getActivatedForRecency();
    
    public void setClearedForRecency(long recency);
    
    public long getClearedForRecency();    
}
