INSERT INTO categories (id, name, description, is_deleted) VALUES (1, 'Fantasy', 'Fantasy category', 0);
INSERT INTO categories (id, name, description, is_deleted) VALUES (2, 'Drama', 'Drama category', 0);
INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (1, 'Fantasy Book 1', 'Fantasy Author 1', '978-83-7506-729-3', 40.00, 'Fantasy Description 1', 'images.com/image1.jpg', 0);
INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (2, 'Fantasy Book 2', 'Fantasy Author 2', '978-83-080-8011-5', 45.50, 'Fantasy Description 2', 'images.com/image2.jpg', 0);
INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (3, 'Drama Book', 'Drama Author', '978-83-240-6401-4', 69.50, 'Drama Description', 'images.com/image3.jpg', 0);
INSERT INTO books_categories (book_id, category_id) VALUES (1, 1);
INSERT INTO books_categories (book_id, category_id) VALUES (2, 1);
INSERT INTO books_categories (book_id, category_id) VALUES (3, 2);