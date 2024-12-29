package com.stdev.futurebox.repository;

import com.stdev.futurebox.connection.DBConnectionUtil;
import com.stdev.futurebox.domain.FutureBox;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FutureBoxRepository {

    public FutureBox save(FutureBox futureBox) throws SQLException {
        String sql =
                "INSERT INTO future_box (uuid, receiver, sender, is_opened, future_movie_type, future_gifticon_type, future_invention_type, created_at) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setObject(1, futureBox.getUuid());
            pstmt.setString(2, futureBox.getReceiver());
            pstmt.setString(3, futureBox.getSender());
            pstmt.setBoolean(4, futureBox.getOpen());
            pstmt.setInt(5, futureBox.getFutureMovieType());
            pstmt.setInt(6, futureBox.getFutureGifticonType());
            pstmt.setInt(7, futureBox.getFutureInventionType());
            pstmt.setTimestamp(8, futureBox.getCreatedTime());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                futureBox.setId(rs.getLong("id"));
            } else {
                throw new SQLException("Creating FutureBox failed, no ID obtained.");
            }
            return futureBox;
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("error", e);
            }
        }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }

    public FutureBox findById(Long id) throws SQLException {
        String sql = "SELECT * FROM future_box WHERE id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, id);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                FutureBox futureBox = new FutureBox();
                futureBox.setId(rs.getLong("id"));
                futureBox.setUuid(java.util.UUID.fromString(rs.getString("uuid")));
                futureBox.setReceiver(rs.getString("receiver"));
                futureBox.setSender(rs.getString("sender"));
                futureBox.setOpen(rs.getBoolean("is_opened"));
                futureBox.setFutureMovieType(rs.getInt("future_movie_type"));
                futureBox.setFutureGifticonType(rs.getInt("future_gifticon_type"));
                futureBox.setFutureInventionType(rs.getInt("future_invention_type"));
                futureBox.setCreatedTime(rs.getTimestamp("created_at"));
                return futureBox;
            } else {
                throw new NoSuchElementException("No such future box with id: " + id);
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, rs);
        }
    }
}

