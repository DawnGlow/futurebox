package com.stdev.futurebox.repository;

import com.stdev.futurebox.domain.FutureNote;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.datasource.DataSourceUtils;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FutureNoteRepository {

    private final DataSource dataSource;

    public FutureNote save(FutureNote note) throws SQLException {
        String sql = "INSERT INTO future_note (box_id, message, encrypted_message) VALUES (?, ?, ?) RETURNING id";
        
        Connection con = DataSourceUtils.getConnection(dataSource);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, note.getBoxId());
            pstmt.setString(2, note.getMessage());
            pstmt.setString(3, note.getEncryptedMessage());
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                note.setId(rs.getLong("id"));
                return note;
            } else {
                throw new SQLException("FutureNote 생성 실패");
            }
        } finally {
            close(null, pstmt, rs);
        }
    }

    public FutureNote findById(Long id) throws SQLException {
        String sql = "SELECT * FROM future_note WHERE id = ?";
        
        Connection con = DataSourceUtils.getConnection(dataSource);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapToNote(rs);
            }
            throw new NoSuchElementException("해당 ID의 FutureNote가 없습니다: " + id);
        } finally {
            close(null, pstmt, rs);
        }
    }

    public FutureNote findByBoxId(Long boxId) throws SQLException {
        String sql = "SELECT * FROM future_note WHERE box_id = ?";
        
        Connection con = DataSourceUtils.getConnection(dataSource);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, boxId);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapToNote(rs);
            }
            return null;
        } finally {
            close(null, pstmt, rs);
        }
    }

    public void update(FutureNote note) throws SQLException {
        String sql = "UPDATE future_note SET message = ?, encrypted_message = ? WHERE id = ?";
        
        Connection con = DataSourceUtils.getConnection(dataSource);
        PreparedStatement pstmt = null;
        
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, note.getMessage());
            pstmt.setString(2, note.getEncryptedMessage());
            pstmt.setLong(3, note.getId());
            pstmt.executeUpdate();
        } finally {
            close(null, pstmt, null);
        }
    }

    public void deleteById(Long id) throws SQLException {
        String sql = "DELETE FROM future_note WHERE id = ?";
        
        Connection con = DataSourceUtils.getConnection(dataSource);
        PreparedStatement pstmt = null;
        
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } finally {
            close(null, pstmt, null);
        }
    }

    public void deleteByBoxId(Long boxId) throws SQLException {
        String sql = "DELETE FROM future_note WHERE box_id = ?";
        
        Connection con = DataSourceUtils.getConnection(dataSource);
        PreparedStatement pstmt = null;
        
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setLong(1, boxId);
            pstmt.executeUpdate();
        } finally {
            close(null, pstmt, null);
        }
    }

    public long count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM future_note";
        
        Connection con = DataSourceUtils.getConnection(dataSource);
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            pstmt = con.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0L;
        } finally {
            close(null, pstmt, rs);
        }
    }

    private FutureNote mapToNote(ResultSet rs) throws SQLException {
        FutureNote note = new FutureNote();
        note.setId(rs.getLong("id"));
        note.setBoxId(rs.getLong("box_id"));
        note.setMessage(rs.getString("message"));
        note.setEncryptedMessage(rs.getString("encrypted_message"));
        return note;
    }

    private void close(Connection con, PreparedStatement pstmt, ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException e) { log.error("ResultSet 닫기 실패", e); }
        }
        if (pstmt != null) {
            try { pstmt.close(); } catch (SQLException e) { log.error("Statement 닫기 실패", e); }
        }
        // Connection은 닫지 않음 - 스프링이 관리하도록 함
    }
}
