/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package org.apache.olingo.jpa.ref.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;

/**
 * JPA Entity illustrating
 * <ol>
 * <li>Inheritance</li>
 * <li>1..N bidirectional relationship with "mappedBy" attribute</li>
 * </ol>
 */
@Entity
@Table(name = "T_CUSTOMER")
public class Customer extends BusinessPartner {

  @Pattern(regexp = "\\(\\d{3}\\)\\d{3}-\\d{4}")
  private String phoneNumber;

  @OneToMany(mappedBy = "customer")
  private List<SalesOrder> orders;

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public List<SalesOrder> getOrders() {
    return orders;
  }

  public void setOrders(List<SalesOrder> orders) {
    this.orders = orders;
  }
}
