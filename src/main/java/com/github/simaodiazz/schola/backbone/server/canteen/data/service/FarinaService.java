package com.github.simaodiazz.schola.backbone.server.canteen.data.service;

import com.github.simaodiazz.schola.backbone.server.canteen.data.model.Farina;
import com.github.simaodiazz.schola.backbone.server.canteen.data.model.FarinaTemporal;
import com.github.simaodiazz.schola.backbone.server.canteen.data.repository.FarinaRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FarinaService {

    private final FarinaRepository farinaRepository;

    public FarinaService(FarinaRepository mealRepository) {
        this.farinaRepository = mealRepository;
    }

    @Cacheable(value = "meals", key = "#id")
    public Farina getMealById(Long id) {
        return farinaRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "mealsByDate", key = "#date.toString()")
    public List<Farina> getMealsByDate(LocalDate date) {
        return farinaRepository.findByDate(date);
    }

    @Cacheable(value = "mealsByDateRange", key = "#startDate.toString() + '_' + #endDate.toString()")
    public List<Farina> getMealsByDateRange(LocalDate startDate, LocalDate endDate) {
        return farinaRepository.findByDateBetween(startDate, endDate);
    }

    @Cacheable(value = "mealsByDateAndType", key = "#date.toString() + '_' + #type")
    public List<Farina> getMealsByDateAndTemporal(LocalDate date, FarinaTemporal temporal) {
        return farinaRepository.findByDateAndTemporal(date, temporal);
    }

    @Cacheable(value = "upcomingMeals", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Farina> getUpcomingMeals(Pageable pageable) {
        return farinaRepository.findUpcomingMeals(LocalDate.now(), pageable);
    }

    @Cacheable(value = "availableMeals", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Farina> getAvailableMealsForReservation(Pageable pageable) {
        return farinaRepository.findAvailableForReservation(LocalDate.now(), pageable);
    }

    @CachePut(value = "meals", key = "#result.id")
    @CacheEvict(value = {"mealsByDate", "mealsByDateRange", "mealsByDateAndType", "upcomingMeals", "availableMeals"}, allEntries = true)
    @Transactional
    public Farina createMeal(Farina meal) {
        List<Farina> existingMeals = farinaRepository.findByDateAndTemporal(meal.getDate(), meal.getTemporal());
        if (!existingMeals.isEmpty()) {
            throw new IllegalArgumentException("Já existe uma refeição do tipo " +
                    meal.getTemporal().getDescription() + " para a data " + meal.getDate());
        }

        if (meal.getReservationDeadline() == null) {
            meal.setReservationDeadline(meal.getDate().minusDays(1));
        }

        return farinaRepository.save(meal);
    }

    @CachePut(value = "meals", key = "#result.id")
    @CacheEvict(value = {"mealsByDate", "mealsByDateRange", "mealsByDateAndType", "upcomingMeals", "availableMeals"}, allEntries = true)
    @Transactional
    public Farina updateMeal(Long id, Farina updatedMeal) {
        Optional<Farina> mealOpt = farinaRepository.findById(id);
        if (mealOpt.isEmpty()) {
            throw new IllegalArgumentException("Refeição não encontrada");
        }
        final Farina meal = mealOpt.get();

        if ((updatedMeal.getDate() != null && !updatedMeal.getDate().equals(meal.getDate())) ||
                (updatedMeal.getTemporal() != null && updatedMeal.getTemporal() != meal.getTemporal())) {

            List<Farina> existingMeals = farinaRepository.findByDateAndTemporal(
                    updatedMeal.getDate() != null ? updatedMeal.getDate() : meal.getDate(),
                    updatedMeal.getTemporal() != null ? updatedMeal.getTemporal() : meal.getTemporal()
            );

            if (!existingMeals.isEmpty() && (existingMeals.size() > 1 || !(existingMeals.getFirst().getId() == id))) {
                throw new IllegalArgumentException("Já existe uma refeição do mesmo tipo para esta data");
            }
        }

        if (updatedMeal.getName() != null) meal.setName(updatedMeal.getName());
        if (updatedMeal.getDescription() != null) meal.setDescription(updatedMeal.getDescription());
        if (updatedMeal.getDate() != null) meal.setDate(updatedMeal.getDate());
        if (updatedMeal.getTemporal() != null) meal.setTemporal(updatedMeal.getTemporal());
        if (updatedMeal.getMaxReservations() > 0) meal.setMaxReservations(updatedMeal.getMaxReservations());
        meal.setVegetarian(updatedMeal.isVegetarian());
        if (updatedMeal.getReservationDeadline() != null) meal.setReservationDeadline(updatedMeal.getReservationDeadline());
        if (updatedMeal.getPrice() > 0) meal.setPrice(updatedMeal.getPrice());

        return farinaRepository.save(meal);
    }

    @CacheEvict(value = {"meals", "mealsByDate", "mealsByDateRange", "mealsByDateAndType", "upcomingMeals", "availableMeals"}, allEntries = true)
    @Transactional
    public void deleteMeal(Long id) {
        if (!farinaRepository.existsById(id)) {
            throw new IllegalArgumentException("Refeição não encontrada");
        }

        farinaRepository.deleteById(id);
    }

    public int getReservationCount(Long mealId) {
        return farinaRepository.countReservationsForMeal(mealId);
    }

    public boolean isMealAvailableForReservation(Long mealId) {
        Optional<Farina> mealOpt = farinaRepository.findById(mealId);
        if (mealOpt.isEmpty()) {
            return false;
        }

        Farina meal = mealOpt.get();
        int reservationCount = getReservationCount(mealId);

        return LocalDate.now().isBefore(meal.getReservationDeadline()) &&
                reservationCount < meal.getMaxReservations();
    }
}