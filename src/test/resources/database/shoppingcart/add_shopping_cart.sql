INSERT INTO users (id, email, password, first_name, last_name, shipping_address, is_deleted)
VALUES (1, 'kuba@gmail.com', 'haslokuby123', 'Jakub', 'Nowak', 'Pilsudskiego 4, 31-489 Krakow, Polska', 0);
INSERT INTO roles (id, name) VALUES (1, 'USER');
INSERT INTO roles (id, name) VALUES (2, 'ADMIN');
INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);
INSERT INTO categories (id, name, description, is_deleted) VALUES (1, 'Comedy',  'Comedy category', 0);
INSERT INTO books (id, title, author, isbn, price, description, cover_image, is_deleted)
VALUES (1, 'Comedy Book', 'Author', '2244-5656677-6787', 35.99, 'Book description', 'images.com/image.jpg', 0);
INSERT INTO books_categories (book_id, category_id) VALUES (1, 1);
INSERT INTO shopping_carts (id, user_id, is_deleted) VALUES (1, 1, 0);
INSERT INTO cart_items (id, shopping_cart_id, book_id, quantity, is_deleted) VALUES (1, 1, 1, 2, 0);