package bpm.vanilla.repository.beans.annotations;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;
import bpm.vanilla.platform.core.repository.Comment;
import bpm.vanilla.platform.core.repository.SecuredComment;
import bpm.vanilla.platform.core.repository.SecuredCommentObject;
import bpm.vanilla.platform.hibernate.HibernateDaoSupport;
import bpm.vanilla.repository.beans.InfoConnections;
import bpm.vanilla.repository.beans.JdbcConnectionProvider;

public class CommentDAO extends HibernateDaoSupport {

	public static final String COMMENT_ID = "COMMENT_ID";
	public static final String COMMENT_OBJECT_ID = "COMMENT_OBJECT_ID";
	public static final String COMMENT_GROUP_ID = "COMMENT_GROUP_ID";
	public static final String COMMENT_TYPE = "COMMENT_TYPE";

	@SuppressWarnings("unchecked")
	public List<Comment> getComment(int groupId, int objectId, int type) {
		List<Object[]> result = null;
		if (groupId == -1) {
			result = (List<Object[]>) getHibernateTemplate().find("from Comment c, SecuredComment secu where " + " secu.commentId = c.id" + " and c.objectId=" + objectId + " and c.type=" + type);
		}
		else {
			result = (List<Object[]>) getHibernateTemplate().find("from Comment c, SecuredComment secu where secu.groupId = " + groupId + " and secu.commentId = c.id" + " and c.objectId=" + objectId + " and c.type=" + type);
		}

		List<Comment> comments = new ArrayList<Comment>();
		if (result != null) {
			for (Object[] obj : result) {
				if (obj[0] != null && obj[0] instanceof Comment) {
					Comment com = (Comment) obj[0];
					com.setSecuredGroups(getSecuredGroup(com.getId()));
					comments.add(com);

				}
			}
		}

		return buildTreeComment(comments);
	}

	@SuppressWarnings("unchecked")
	public List<SecuredComment> getSecuredGroup(int commentId) {
		return (List<SecuredComment>) getHibernateTemplate().find("from SecuredComment where commentId = " + commentId);
	}

	private List<Comment> buildTreeComment(List<Comment> comments) {
		HashMap<Integer, Comment> hashComs = new HashMap<Integer, Comment>();
		List<Comment> commentsTree = new ArrayList<Comment>();
		List<Comment> parentNotFound = new ArrayList<Comment>();

		for (Comment com : comments) {
			hashComs.put(com.getId(), com);
			if (com.getParentId() == null) {
				commentsTree.add(com);
			}
			else {
				if (hashComs.get(com.getParentId()) != null) {
					hashComs.get(com.getParentId()).addChild(com);
				}
				else {
					parentNotFound.add(com);
				}
			}
		}

		if (!parentNotFound.isEmpty()) {
			for (Comment com : parentNotFound) {
				if (hashComs.get(com.getParentId()) != null) {
					hashComs.get(com.getParentId()).addChild(com);
				}
			}
		}

		return commentsTree;
	}

	public int save(Comment d, List<Integer> groupIds) {
		d.setId((Integer) getHibernateTemplate().save(d));

		if (groupIds != null) {
			for (Integer groupId : groupIds) {
				SecuredComment sec = new SecuredComment();
				sec.setCommentId(d.getId());
				sec.setGroupId(groupId);
				getHibernateTemplate().save(sec);
			}
		}

		return d.getId();
	}

	@SuppressWarnings("unchecked")
	public void delete(Comment d) {
		List<SecuredComment> results = getSecuredGroup(d.getId());
		if (results != null) {
			for (SecuredComment sec : results) {
				getHibernateTemplate().delete(sec);
			}
		}

		List<Comment> comments = (List<Comment>) getHibernateTemplate().find("from Comment where parentId = " + d.getId());
		if (comments != null && !comments.isEmpty()) {
			for (Comment com : comments) {
				delete(com);
			}
		}

		getHibernateTemplate().delete(d);
	}

	@SuppressWarnings("unchecked")
	public void deleteComments(int objectId, int type) {
		List<Comment> results = (List<Comment>) getHibernateTemplate().find("from Comment where objectId = " + objectId + " and type=" + type);
		if (results != null) {
			for (Comment com : results) {
				delete(com);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void update(Comment d, List<Integer> groupIds) {
		List<SecuredComment> results = (List<SecuredComment>) getHibernateTemplate().find("from SecuredComment where commentId = " + d.getId());
		if (results != null) {
			for (SecuredComment sec : results) {
				getHibernateTemplate().delete(sec);
			}
		}

		if (groupIds != null) {
			for (Integer groupId : groupIds) {
				SecuredComment sec = new SecuredComment();
				sec.setCommentId(d.getId());
				sec.setGroupId(groupId);
				getHibernateTemplate().save(sec);
			}
		}

		getHibernateTemplate().update(d);
	}

	public void save(SecuredCommentObject secObject) {
		getHibernateTemplate().save(secObject);
	}

	public List<SecuredCommentObject> getSecuredCommentObjects(int repositoryId, int objectId, int type) throws Exception {
		InfoConnections info = JdbcConnectionProvider.getInstance().getInfoConnection(repositoryId);
		
		VanillaJdbcConnection connection = ConnectionManager.getInstance().getJdbcConnection(info.getRepositoryDBUrl(), info.getLogin(), info.getPassword(), info.getDriver());
		
		VanillaPreparedStatement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("select securedcom.id as " + SecuredCommentObject.COMMENT_ID 
				+ ", securedcom.group_id as " + SecuredCommentObject.COMMENT_GROUP_ID 
				+ ", securedcom.object_id as " + SecuredCommentObject.COMMENT_OBJECT_ID 
				+ ", securedcom.type as " + SecuredCommentObject.COMMENT_TYPE 
				+ " from rpy_secured_annotations_object securedcom where securedcom.object_id=" + objectId + " and securedcom.type=" + type);
		List<SecuredCommentObject> results = myConverter(resultSet);
		resultSet.close();
		statement.close();
		ConnectionManager.getInstance().returnJdbcConnection(connection);
		
		return results;

//		 List<SecuredCommentObject> results = (List<SecuredCommentObject>)
//		 getHibernateTemplate().find("from SecuredCommentObject where"
//		 + " objectId=" + objectId
//		 + " and type=" + type);
//		return results;
	}

	private List<SecuredCommentObject> myConverter(ResultSet rs) throws Exception {
		List<SecuredCommentObject> secs = new ArrayList<SecuredCommentObject>();
		while (rs.next()) {
			int id = rs.getInt(SecuredCommentObject.COMMENT_ID);
			Integer groupId = rs.getInt(SecuredCommentObject.COMMENT_GROUP_ID);
			Integer objectId = rs.getInt(SecuredCommentObject.COMMENT_OBJECT_ID);
			Integer type = rs.getInt(SecuredCommentObject.COMMENT_TYPE);
			secs.add(new SecuredCommentObject(id, objectId, groupId, type));
		}
		return secs;
	}

	@SuppressWarnings("unchecked")
	public void deleteSecuredCommentObject(int groupId, int objectId, int type) {
		List<SecuredCommentObject> results = (List<SecuredCommentObject>) getHibernateTemplate().find("from SecuredCommentObject where groupId = " + groupId + " and objectId=" + objectId + " and type=" + type);
		if (results != null) {
			for (SecuredCommentObject secCObj : results) {
				getHibernateTemplate().delete(secCObj);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void deleteSecuredCommentObjects(int objectId, int type) {
		List<SecuredCommentObject> results = (List<SecuredCommentObject>) getHibernateTemplate().find("from SecuredCommentObject where" + " objectId=" + objectId + " and type=" + type);
		if (results != null) {
			for (SecuredCommentObject secCObj : results) {
				getHibernateTemplate().delete(secCObj);
			}
		}
	}

}
