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
package org.apache.accumulo.master.tableOps.namespace.create;

import java.util.Map;

import org.apache.accumulo.core.data.NamespaceId;
import org.apache.accumulo.fate.Repo;
import org.apache.accumulo.master.Master;
import org.apache.accumulo.master.tableOps.MasterRepo;
import org.apache.accumulo.master.tableOps.Utils;

public class CreateNamespace extends MasterRepo {
  private static final long serialVersionUID = 1L;

  private NamespaceInfo namespaceInfo;

  public CreateNamespace(String user, String namespaceName, Map<String,String> props) {
    namespaceInfo = new NamespaceInfo();
    namespaceInfo.namespaceName = namespaceName;
    namespaceInfo.user = user;
    namespaceInfo.props = props;
  }

  @Override
  public long isReady(long tid, Master environment) {
    return 0;
  }

  @Override
  public Repo<Master> call(long tid, Master master) throws Exception {
    Utils.getIdLock().lock();
    try {
      namespaceInfo.namespaceId = Utils.getNextId(namespaceInfo.namespaceName, master.getContext(),
          NamespaceId::of);
      return new SetupNamespacePermissions(namespaceInfo);
    } finally {
      Utils.getIdLock().unlock();
    }

  }

  @Override
  public void undo(long tid, Master env) {
    // nothing to do, the namespace id was allocated!
  }

}
