const path = require('path');

module.exports = {
  entry: './src/index.ts',
  mode: 'development',
  devServer: {
    port: 8080,
    historyApiFallback: true,
    compress: false,
  },
  module: {
    rules: [
      {
        test: /\.tsx?$/, // Handle TypeScript and TSX files
        use: 'ts-loader',
        exclude: /node_modules/,
      },
      {
        test: /\.css$/i, // Handle CSS files
        use: ['style-loader', 'css-loader'], // Apply loaders
      },
    ],
  },
  resolve: {
    extensions: ['.tsx', '.ts', '.js'], // Resolve these extensions
  },
  output: {
    filename: 'bundle.js', // Output bundle file
    path: path.resolve(__dirname, 'dist'), // Output path
  },
};
