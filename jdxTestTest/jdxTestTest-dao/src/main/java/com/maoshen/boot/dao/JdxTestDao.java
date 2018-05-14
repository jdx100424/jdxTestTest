package com.maoshen.boot.dao;

import com.maoshen.component.dao.annotation.JdxDao;
import com.maoshen.component.dao.annotation.JdxDaoSqlMapper;
import com.maoshen.component.dao.annotation.JdxDaoSqlTypeQuery;
import com.maoshen.component.dao.annotation.JdxDaoSqlTypeUpdate;

@JdxDao("jdxTestDao")
public interface JdxTestDao {
	@JdxDaoSqlMapper("select count(1) from jdx_test_table where id = ?")
	@JdxDaoSqlTypeQuery
	public Integer getCount(Long id);

	@JdxDaoSqlMapper("insert into jdx_test_table(id,name) values(?,?)")
	@JdxDaoSqlTypeUpdate
	public void insert(Long id,String name);
}
