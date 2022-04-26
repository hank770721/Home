package com.hkma.home.system.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.core.log.LogMessage;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;

import com.hkma.home.system.repository.SystemPersistentTokenRepository;

public class SystemJdbcTokenRepositoryImpl extends JdbcDaoSupport implements SystemPersistentTokenRepository {
	public static final String DEF_TOKEN_BY_SERIES_SQL = "select username,series,token,last_used from system_logins where series = ?";

	public static final String DEF_INSERT_TOKEN_SQL = "insert into system_logins (username, series, token, last_used) values(?,?,?,?)";

	public static final String DEF_UPDATE_TOKEN_SQL = "update system_logins set token = ?, last_used = ? where series = ?";

	public static final String DEF_REMOVE_USER_TOKENS_SQL = "delete from system_logins where username = ?";

	private String tokensBySeriesSql = DEF_TOKEN_BY_SERIES_SQL;

	private String insertTokenSql = DEF_INSERT_TOKEN_SQL;

	private String updateTokenSql = DEF_UPDATE_TOKEN_SQL;

	private String removeUserTokensSql = DEF_REMOVE_USER_TOKENS_SQL;

	@Override
	protected void initDao() {
		//if (this.createTableOnStartup) {
		//	getJdbcTemplate().execute(CREATE_TABLE_SQL);
		//}
	}

	@Override
	public void createNewToken(PersistentRememberMeToken token) {
		getJdbcTemplate().update(this.insertTokenSql, token.getUsername(), token.getSeries(), token.getTokenValue(),
				token.getDate());
	}

	@Override
	public void updateToken(String series, String tokenValue, Date lastUsed) {
		getJdbcTemplate().update(this.updateTokenSql, tokenValue, lastUsed, series);
	}

	@Override
	public PersistentRememberMeToken getTokenForSeries(String seriesId) {
		try {
			return getJdbcTemplate().queryForObject(this.tokensBySeriesSql, this::createRememberMeToken, seriesId);
		} catch (EmptyResultDataAccessException ex) {
			this.logger.debug(LogMessage.format("Querying token for series '%s' returned no results.", seriesId), ex);
		} catch (IncorrectResultSizeDataAccessException ex) {
			this.logger.error(LogMessage.format(
					"Querying token for series '%s' returned more than one value. Series" + " should be unique",
					seriesId));
		} catch (DataAccessException ex) {
			this.logger.error("Failed to load token for series " + seriesId, ex);
		}
		return null;
	}

	private PersistentRememberMeToken createRememberMeToken(ResultSet rs, int rowNum) throws SQLException {
		return new PersistentRememberMeToken(rs.getString(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4));
	}

	@Override
	public void removeUserTokens(String username) {
		getJdbcTemplate().update(this.removeUserTokensSql, username);
	}
	
	@Override
	public void removeTokens(String seriesId) {
		getJdbcTemplate().update("delete from system_logins where series = ?", seriesId);
	}
}
