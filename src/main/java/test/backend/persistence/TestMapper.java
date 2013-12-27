package test.backend.persistence;

import test.backend.model.Test;

public interface TestMapper {
	Test selectOne(Long id);
}
