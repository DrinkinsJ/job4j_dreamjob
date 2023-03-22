package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import javax.annotation.concurrent.ThreadSafe;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryVacancyRepository implements VacancyRepository {

    private final AtomicInteger nextId = new AtomicInteger(1);

    private final Map<Integer, Vacancy> vacancies = new HashMap<>();

    private MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer", "Intern description", LocalDateTime.of(2022, 12, 31, 16, 30)));
        save(new Vacancy(0, "Junior Java Developer", "Junior description", LocalDateTime.of(2022, 6, 16, 15, 20)));
        save(new Vacancy(0, "Junior+ Java Developer", "Junior+ description", LocalDateTime.of(2021, 11, 30, 14, 10)));
        save(new Vacancy(0, "Middle Java Developer", "Middle description", LocalDateTime.of(2021, 5, 15, 13, 33)));
        save(new Vacancy(0, "Middle+ Java Developer", "Middle+ description", LocalDateTime.of(2020, 10, 14, 12, 21)));
        save(new Vacancy(0, "Senior Java Developer", "Senior description", LocalDateTime.of(2020, 4, 29, 11, 49)));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId.getAndIncrement());
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(), (id, oldVacancy) ->
                new Vacancy(oldVacancy.getId(), vacancy.getTitle(), vacancy.getDescription(), vacancy.getCreationDate())) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }

}