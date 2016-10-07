/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.mapping;

import javax.sql.DataSource;

import cn.xianyijun.orm.transaction.TransactionFactory;

public final class Environment {
	private final String id;
	private final TransactionFactory transactionFactory;
	private final DataSource dataSource;

	public Environment(String id, TransactionFactory transactionFactory, DataSource dataSource) {
		if (id == null) {
			throw new IllegalArgumentException("Parameter 'id' must not be null");
		}
		if (transactionFactory == null) {
			throw new IllegalArgumentException("Parameter 'transactionFactory' must not be null");
		}
		this.id = id;
		if (dataSource == null) {
			throw new IllegalArgumentException("Parameter 'dataSource' must not be null");
		}
		this.transactionFactory = transactionFactory;
		this.dataSource = dataSource;
	}

	public static class Builder {
		private String id;
		private TransactionFactory transactionFactory;
		private DataSource dataSource;

		public Builder(String id) {
			this.id = id;
		}

		public Builder transactionFactory(TransactionFactory transactionFactory) {
			this.transactionFactory = transactionFactory;
			return this;
		}

		public Builder dataSource(DataSource dataSource) {
			this.dataSource = dataSource;
			return this;
		}

		public String id() {
			return this.id;
		}

		public Environment build() {
			return new Environment(this.id, this.transactionFactory, this.dataSource);
		}
	}

	public String getId() {
		return id;
	}

	public TransactionFactory getTransactionFactory() {
		return transactionFactory;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
	
}
