package cn.enilu.elm.api.repository;

import cn.enilu.elm.api.entity.BaseEntity;
import com.mongodb.WriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * Created  on 2017/12/29 0029.
 *
 * @author zt
 */
@Repository
public class BaseDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void save(BaseEntity entity) {
        mongoTemplate.save(entity);
    }

    public void save(Object data, String collectionName) {
        mongoTemplate.save(data, collectionName);

    }

    public void delete(Long id, String collectionName) {
        mongoTemplate.remove(Query.query(Criteria.where("id").is(id)), collectionName);
    }

    public void delete(String collectionName, Map<String, Object> keyValues) {
        mongoTemplate.remove(Query.query(criteria(keyValues)), collectionName);
    }

    public void update(BaseEntity entity) {
        mongoTemplate.save(entity);
    }

    public WriteResult update(Long id, String collectionName, Map<String, Object> keyValues) {
        Update update = null;
        for (Map.Entry<String, Object> entry : keyValues.entrySet()) {
            if (update == null) {
                update = Update.update(entry.getKey(), entry.getValue());
            } else {
                update.set(entry.getKey(), entry.getValue());
            }
        }
        return mongoTemplate.updateFirst(Query.query(Criteria.where("id").is(id)), update, collectionName);
    }

    public <T, P> T findOne(Class<T> klass, P key) {
        return findOne(klass, "id", key);
    }

    public <T> T findOne(Class<T> klass, String key, Object value) {
        return mongoTemplate.findOne(Query.query(Criteria.where(key).is(value)), klass);
    }

    public Object findOne(Long id, String collectionName) {
        return mongoTemplate.findOne(Query.query(Criteria.where("id").is(id)), Map.class, collectionName);
    }


    public Map findOne(String collectionName, Object... extraKeyValues) {
        Criteria criteria = criteria(extraKeyValues);
        if (criteria == null) {
            List<Map> list = mongoTemplate.findAll(Map.class, collectionName);
            if (!CollectionUtils.isEmpty(list)) {
                return list.get(0);
            }
            return null;
        }
        return mongoTemplate.findOne(Query.query(criteria), Map.class, collectionName);
    }

    public <T> T findOne(Class<T> klass, Object... keyValues) {
        Criteria criteria = criteria(keyValues);

        if (criteria == null) {
            List<T> list = mongoTemplate.findAll(klass);
            if (list != null) {
                return list.get(0);
            }
            return null;
        }
        return mongoTemplate.findOne(Query.query(criteria), klass);
    }


    public <T> List<T> findAll(Class<T> klass) {
        return mongoTemplate.findAll(klass);
    }

    public <T> List<T> findAll(Class<T> klass, Object... keyValues) {
        Criteria criteria = criteria(keyValues);
        return mongoTemplate.find(Query.query(criteria), klass);
    }

    public <T> List<T> findAll(Class<T> klass, Map<String, Object> keyValues) {
        Criteria criteria = criteria(keyValues);
        return mongoTemplate.find(Query.query(criteria), klass);
    }

    public List findAll(String collection) {
        return mongoTemplate.findAll(Map.class, collection);
    }

    public List<Map> findAll(String collectionName, Object... keyValues) {
        Criteria criteria = criteria(keyValues);
        return mongoTemplate.find(Query.query(criteria), Map.class, collectionName);
    }

    public GeoResults<Map> near(double x, double y, String collectionName) {
        return mongoTemplate.geoNear(NearQuery.near(x, y), Map.class, collectionName);
    }

    public long count(Class klass) {
        return count(klass, null);
    }

    public long count(Class klass, Map<String, Object> params) {
        Criteria criteria = criteria(params);
        if (criteria == null) {
            return mongoTemplate.count(null, klass);
        } else {
            return mongoTemplate.count(Query.query(criteria), klass);
        }
    }

    public long count(String collection) {
        return mongoTemplate.count(null, collection);
    }

    public long count(String collection, Map<String, Object> params) {
        Criteria criteria = criteria(params);
        if (criteria == null) {
            return mongoTemplate.count(null, collection);
        } else {
            return mongoTemplate.count(Query.query(criteria), collection);
        }
    }


    private Criteria criteria(Map<String, Object> map) {
        Criteria criteria = null;
        if (map != null) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (criteria == null) {
                    criteria = Criteria.where(entry.getKey()).is(entry.getValue());
                } else {
                    criteria.and(entry.getKey()).is(entry.getValue());
                }
            }
        }
        return criteria;
    }

    private Criteria criteria(Object... extraKeyValues) {
        Criteria criteria = null;
        if (extraKeyValues.length % 2 != 0) {
            throw new IllegalArgumentException();
        } else {
            for (int i = 0; i < extraKeyValues.length; i += 2) {
                Object k = extraKeyValues[i];
                Object v = extraKeyValues[i + 1];
                if (i == 0) {
                    criteria = Criteria.where(k.toString()).is(v);
                } else {
                    criteria.and(k.toString()).is(v);
                }
            }

        }
        return criteria;
    }


}
