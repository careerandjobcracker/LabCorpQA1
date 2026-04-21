/*
  # Create QA Test Tracking Tables

  1. New Tables
    - `test_runs`
      - `id` (uuid, primary key) - unique run identifier
      - `run_name` (text) - descriptive name for the test run
      - `status` (text) - passed, failed, running, pending
      - `total_scenarios` (integer) - total number of scenarios
      - `passed_scenarios` (integer) - number passed
      - `failed_scenarios` (integer) - number failed
      - `skipped_scenarios` (integer) - number skipped
      - `duration_ms` (integer) - total execution time in milliseconds
      - `browser` (text) - browser used (e.g., Chrome)
      - `environment` (text) - test environment (e.g., QA, Staging)
      - `triggered_by` (text) - who or what triggered the run
      - `created_at` (timestamptz) - when the run was created

    - `test_scenarios`
      - `id` (uuid, primary key) - unique scenario identifier
      - `test_run_id` (uuid, FK) - which run this belongs to
      - `scenario_name` (text) - name of the scenario
      - `feature_name` (text) - parent feature name
      - `status` (text) - passed, failed, skipped
      - `duration_ms` (integer) - execution time in ms
      - `error_message` (text) - failure message if any
      - `tags` (text[]) - cucumber tags
      - `created_at` (timestamptz) - when this was created

  2. Security
    - Enable RLS on both tables
    - Add policies for public read access (dashboard is public-facing)
    - Add policies for authenticated insert/update/delete
*/

CREATE TABLE IF NOT EXISTS test_runs (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  run_name text NOT NULL DEFAULT '',
  status text NOT NULL DEFAULT 'pending',
  total_scenarios integer NOT NULL DEFAULT 0,
  passed_scenarios integer NOT NULL DEFAULT 0,
  failed_scenarios integer NOT NULL DEFAULT 0,
  skipped_scenarios integer NOT NULL DEFAULT 0,
  duration_ms integer NOT NULL DEFAULT 0,
  browser text NOT NULL DEFAULT 'Chrome',
  environment text NOT NULL DEFAULT 'QA',
  triggered_by text NOT NULL DEFAULT 'Manual',
  created_at timestamptz DEFAULT now()
);

ALTER TABLE test_runs ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Anyone can view test runs"
  ON test_runs
  FOR SELECT
  TO anon, authenticated
  USING (true);

CREATE POLICY "Authenticated users can insert test runs"
  ON test_runs
  FOR INSERT
  TO authenticated
  WITH CHECK (auth.uid() IS NOT NULL);

CREATE POLICY "Authenticated users can update test runs"
  ON test_runs
  FOR UPDATE
  TO authenticated
  USING (auth.uid() IS NOT NULL)
  WITH CHECK (auth.uid() IS NOT NULL);

CREATE POLICY "Authenticated users can delete test runs"
  ON test_runs
  FOR DELETE
  TO authenticated
  USING (auth.uid() IS NOT NULL);


CREATE TABLE IF NOT EXISTS test_scenarios (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  test_run_id uuid NOT NULL REFERENCES test_runs(id),
  scenario_name text NOT NULL DEFAULT '',
  feature_name text NOT NULL DEFAULT '',
  status text NOT NULL DEFAULT 'pending',
  duration_ms integer NOT NULL DEFAULT 0,
  error_message text NOT NULL DEFAULT '',
  tags text[] NOT NULL DEFAULT '{}',
  created_at timestamptz DEFAULT now()
);

ALTER TABLE test_scenarios ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Anyone can view test scenarios"
  ON test_scenarios
  FOR SELECT
  TO anon, authenticated
  USING (true);

CREATE POLICY "Authenticated users can insert test scenarios"
  ON test_scenarios
  FOR INSERT
  TO authenticated
  WITH CHECK (auth.uid() IS NOT NULL);

CREATE POLICY "Authenticated users can update test scenarios"
  ON test_scenarios
  FOR UPDATE
  TO authenticated
  USING (auth.uid() IS NOT NULL)
  WITH CHECK (auth.uid() IS NOT NULL);

CREATE POLICY "Authenticated users can delete test scenarios"
  ON test_scenarios
  FOR DELETE
  TO authenticated
  USING (auth.uid() IS NOT NULL);
