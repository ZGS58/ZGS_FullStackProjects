package ZGS.backend.service;

import ZGS.backend.dto.ReviewDto;
import ZGS.backend.entity.Product;
import ZGS.backend.entity.Review;
import ZGS.backend.entity.Room;
import ZGS.backend.entity.User;
import ZGS.backend.mapper.ReviewMapper;
import ZGS.backend.repository.ProductRepository;
import ZGS.backend.repository.ReviewRepository;
import ZGS.backend.repository.RoomRepository;
import ZGS.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final RoomRepository roomRepository;
    private final ReviewMapper reviewMapper;


    // 獲取商品的所有評論

    public List<ReviewDto> getProductReviews(Long productId) {
        return reviewRepository.findByProductId(productId)
                .stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    // 獲取房間的所有評論

    public List<ReviewDto> getRoomReviews(Long roomId) {
        return reviewRepository.findByRoomId(roomId)
                .stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    // 新增商品評論（根據 username）
    
    @Transactional
    public ReviewDto createProductReview(String username, Long productId, Integer rating, String comment) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        validateRating(rating);

        Review review = Review.builder()
                .rating(rating)
                .comment(comment)
                .user(user)
                .product(product)
                .build();

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toDto(savedReview);
    }

    // 新增房間評論（根據 username）

    @Transactional
    public ReviewDto createRoomReview(String username, Long roomId, Integer rating, String comment) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在"));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("房間不存在"));

        validateRating(rating);

        Review review = Review.builder()
                .rating(rating)
                .comment(comment)
                .user(user)
                .room(room)
                .build();

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toDto(savedReview);
    }

    // 刪除評論（本人或管理員）

    @Transactional
    public void deleteReview(Long reviewId, String username, boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("評論不存在"));

        // 檢查權限：必須是本人或管理員
        if (!review.getUser().getUsername().equals(username) && !isAdmin) {
            throw new IllegalArgumentException("無權刪除此評論");
        }

        reviewRepository.delete(review);
    }

    // 驗證評分是否有效（1-5分）
    
    private void validateRating(Integer rating) {
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("評分必須在 1 到 5 之間");
        }
    }
}
