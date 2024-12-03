package itmo.course.coursework.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import itmo.course.coursework.exception.BadRequestException;
import itmo.course.coursework.domain.Category;
import itmo.course.coursework.repository.CategoryRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category createCategory(String name, String description) {
        if (categoryRepository.existsByName(name)) {
            throw new BadRequestException("Категория с таким именем уже существует");
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new BadRequestException("Категория не найдена"));
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category updateCategory(Long id, String name, String description) {
        Category category = getCategoryById(id);
        
        if (name != null && !name.equals(category.getName())) {
            if (categoryRepository.existsByName(name)) {
                throw new BadRequestException("Категория с таким именем уже существует");
            }
            category.setName(name);
        }
        
        if (description != null) {
            category.setDescription(description);
        }
        
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
} 