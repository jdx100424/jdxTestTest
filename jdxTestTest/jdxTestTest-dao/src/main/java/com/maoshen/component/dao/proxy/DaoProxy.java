package com.maoshen.component.dao.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.maoshen.component.dao.annotation.JdxDaoSqlMapper;
import com.maoshen.component.dao.annotation.JdxDaoSqlTypeQuery;
import com.maoshen.component.dao.annotation.JdxDaoSqlTypeUpdate;
import com.maoshen.component.dao.sql.DBUtil;

public class DaoProxy {
	private static final DaoProxy DEFAULT_DAO_PROXY = new DaoProxy();

	public static DaoProxy getDefaultDaoProxy() {
		return DEFAULT_DAO_PROXY;
	}

	public Object run(Method method, Object[] args) throws Exception {
		// 根据头顶的@JdxDaoSqlMapper，获取SQL，并注入占位符的参数值
		JdxDaoSqlMapper jdxDaoSqlMapper = method.getAnnotation(JdxDaoSqlMapper.class);
		JdxDaoSqlTypeQuery jdxDaoSqlTypeQuery = method.getAnnotation(JdxDaoSqlTypeQuery.class);
		JdxDaoSqlTypeUpdate jdxDaoSqlTypeUpdate = method.getAnnotation(JdxDaoSqlTypeUpdate.class);
		if (jdxDaoSqlMapper == null || StringUtils.isBlank(jdxDaoSqlMapper.value())) {
			throw new Exception("no sql string");
		}
		if (jdxDaoSqlTypeQuery == null && jdxDaoSqlTypeUpdate == null) {
			throw new Exception("no sql type");
		}
		String sql = jdxDaoSqlMapper.value();
		// 占位符填充参数
		PreparedStatement pstmt = null;
		try {
			Connection conn = DBUtil.getConnection();
			pstmt = conn.prepareStatement(sql);
			if (args != null && args.length > 0) {
				int i = 1;
				for (Object o : args) {
					try {
						if (Class.forName(o.getClass().getName()).newInstance() instanceof java.lang.String) {
							pstmt.setString(i, o.toString());
						}
					} catch (Exception e) {

					}

					try {
						if (o.getClass().getConstructor(int.class).newInstance(1) instanceof java.lang.Integer) {
							pstmt.setInt(i, Integer.parseInt(o.toString()));
						}
					} catch (Exception e) {

					}

					try {
						if (o.getClass().getConstructor(long.class).newInstance(1L) instanceof java.lang.Long) {
							pstmt.setLong(i, Long.parseLong(o.toString()));
						}
					} catch (Exception e) {

					}

					try {
						if (o.getClass().getConstructor(double.class).newInstance(1D) instanceof java.lang.Double) {
							pstmt.setDouble(i, Double.parseDouble(o.toString()));
						}
					} catch (Exception e) {

					}

					try {
						if (o.getClass().getConstructor(float.class).newInstance(1F) instanceof java.lang.Float) {
							pstmt.setFloat(i, Float.parseFloat(o.toString()));
						}
					} catch (Exception e) {

					}

					try {
						if (o.getClass().getConstructor(boolean.class).newInstance(true) instanceof java.lang.Boolean) {
							pstmt.setBoolean(i, Boolean.parseBoolean(o.toString()));
						}
					} catch (Exception e) {

					}

					try {
						if (o.getClass().getConstructor(byte.class).newInstance(true) instanceof java.lang.Byte) {
							pstmt.setByte(i, Byte.parseByte(o.toString()));
						}
					} catch (Exception e) {

					}

					try {
						if (o.getClass().getConstructor(short.class).newInstance(true) instanceof java.lang.Short) {
							pstmt.setShort(i, Short.parseShort(o.toString()));
						}
					} catch (Exception e) {

					}

					try {
						if (o.getClass().getConstructor(char.class).newInstance(true) instanceof java.lang.Character) {
							pstmt.setString(i, o.toString());
						}
					} catch (Exception e) {

					}
					i++;
				}
			}
			if (jdxDaoSqlTypeQuery != null) {
				Class<?> returnClass = method.getReturnType();
				if (returnClass != null) {
					boolean isList = false;
					for (Class<?> inter : returnClass.getInterfaces()) {
						if (List.class.getName().equals(inter.getName())) {
							// 暂时只认List
							isList = true;
						} else {

						}
					}

					ResultSet resultSet = pstmt.executeQuery();
					if (isList) {

					} else {
						ResultSetMetaData metaData = resultSet.getMetaData();
						Object resultObject = null;
						
						int count = metaData.getColumnCount();// 获取ResultSet中数据的列数
						resultSet.next();// 移动指针
						// 遍历获的每一列的列名,采用反射机制设置值,暂时返回的列名和实体类的一致
						for (int i = 1; i <= count; i++) {
							String name = metaData.getColumnName(i);
							Object o = resultSet.getObject(i);
							boolean isBaseType = false;
							try {
								if (Class.forName(returnClass.getName()).newInstance() instanceof java.lang.String) {
									resultObject = (o == null ? null : o.toString());
									isBaseType = true;
								}
							} catch (Exception e) {

							}

							try {
								if (returnClass.getConstructor(int.class).newInstance(1) instanceof java.lang.Integer) {
									resultObject = (o == null ? null : Integer.parseInt(o.toString()));
									isBaseType = true;
								}
							} catch (Exception e) {

							}

							try {
								if (returnClass.getConstructor(long.class).newInstance(1L) instanceof java.lang.Long) {
									resultObject = (o == null ? null : Long.parseLong(o.toString()));
									isBaseType = true;
								}
							} catch (Exception e) {

							}

							try {
								if (returnClass.getConstructor(double.class)
										.newInstance(1D) instanceof java.lang.Double) {
									resultObject = (o == null ? null : Double.parseDouble(o.toString()));
									isBaseType = true;
								}
							} catch (Exception e) {

							}

							try {
								if (returnClass.getConstructor(float.class)
										.newInstance(1F) instanceof java.lang.Float) {
									resultObject = (o == null ? null : Float.parseFloat(o.toString()));
									isBaseType = true;
								}
							} catch (Exception e) {

							}

							try {
								if (returnClass.getConstructor(boolean.class)
										.newInstance(true) instanceof java.lang.Boolean) {
									resultObject = (o == null ? null : Boolean.parseBoolean(o.toString()));
									isBaseType = true;
								}
							} catch (Exception e) {

							}

							try {
								if (returnClass.getConstructor(byte.class)
										.newInstance(true) instanceof java.lang.Byte) {
									resultObject = (o == null ? null : Byte.parseByte(o.toString()));
									isBaseType = true;
								}
							} catch (Exception e) {

							}

							try {
								if (returnClass.getConstructor(short.class)
										.newInstance(true) instanceof java.lang.Short) {
									resultObject = (o == null ? null : Short.parseShort(o.toString()));
									isBaseType = true;
								}
							} catch (Exception e) {

							}

							try {
								if (returnClass.getConstructor(char.class)
										.newInstance(true) instanceof java.lang.Character) {
									resultObject = (o == null ? null : new Character(o.toString().toCharArray()[0]));
									isBaseType = true;
								}
							} catch (Exception e) {

							}

							if (isBaseType == false) {
								Field field = returnClass.getDeclaredField(name);
								field.setAccessible(true);
								field.set(resultObject, o);// 封装数据进入JavaBean
							}else {
								//非LIST的只要第一个值
								break;
							}
						}
						return resultObject;
					}
				}
			} else if (jdxDaoSqlTypeUpdate != null) {
				int rows = pstmt.executeUpdate();
				if (rows != 0) {
					System.out.println("Update product success!");
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			// 关闭连接
			DBUtil.closeConnection(pstmt);
		}
	}

}
